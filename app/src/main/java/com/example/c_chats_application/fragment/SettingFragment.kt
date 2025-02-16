package com.example.c_chats_application.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.c_chats_application.R
import com.example.c_chats_application.compose.DialogCompose
import com.example.c_chats_application.config.COMMON
import com.example.c_chats_application.databinding.FragmentLayoutSettingBinding
import com.example.c_chats_application.screen.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class SettingFragment : Fragment() {
    private lateinit var binding: FragmentLayoutSettingBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var TAG = "ZZSettingFragmentZZ"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLayoutSettingBinding.inflate(inflater, container, false)
        Log.e(TAG, "onCreateView: Setting")

        binding.btnLogOut.setOnClickListener {
            DialogCompose(
                "Notification !",
                "Are you sure to log out?",
                requireContext(),
                onConfirm = {
                    db.collection("user").document(auth.currentUser?.uid.toString()).update(
                        "status", COMMON.statusOffline,
                        "lastSeen", FieldValue.serverTimestamp()
                    ).addOnSuccessListener {
                        auth.signOut()
                        Toast.makeText(requireContext(), "Logout successfully", Toast.LENGTH_SHORT)
                            .show()
                        startActivity(Intent(requireActivity(), LoginActivity::class.java))
                        requireActivity().finish()
                    }.addOnFailureListener {
                        Log.e(TAG, "onCreateView: $it --- msg: ${it.message}")
                    }
                },
                onCancel = {

                }
            )

        }
        setUpUi()
        return binding.root
    }

    private fun setUpUi() {
        val idUser = auth.currentUser?.uid ?: ""
        db.collection("user").document(idUser).get()
            .addOnSuccessListener {
                val name = it.getString("name").toString()
                val image = it.getString("image").toString()
                binding.tvNameUser.text = name
                if (isAdded && activity != null && !requireActivity().isDestroyed) {
                    Glide.with(requireContext())
                        .load(image)
                        .placeholder(R.drawable.icon_avata_default)
                        .circleCrop()
                        .into(binding.ivAvatar)
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "setUpUi: $it -- ${it.message}")
            }

    }
}