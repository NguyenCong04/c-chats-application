package com.example.c_chats_application.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.c_chats_application.adapter.UserStatusAdapter
import com.example.c_chats_application.databinding.FragmentLayoutHomeBinding
import com.example.c_chats_application.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentLayoutHomeBinding
    private var TAG = "ZZHomeFragmentZZ"
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val listUser = mutableListOf<UserModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLayoutHomeBinding.inflate(inflater, container, false)
        Log.e(TAG, "onCreateView: Home")

        fetchUser()

        return binding.root
    }

    private fun setUpRecyclerView(userList: MutableList<UserModel>, context: Context?) {
        val userStatusAdapter = UserStatusAdapter(userList)
        val layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rcvStatusUser.layoutManager = layoutManager
        binding.rcvStatusUser.adapter = userStatusAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchUser() {
        val userId = auth.currentUser?.uid.toString()

        db.collection("user")
            .whereNotEqualTo("idUser", userId) // Loại bỏ userId khỏi danh sách
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e(TAG, "Lỗi khi lấy dữ liệu: ", error)
                    return@addSnapshotListener
                }
                listUser.clear()
                value?.documents?.forEach { document ->
                    val user = document.toObject(UserModel::class.java)
                    Log.d(TAG, "fetchUser: $user")
                    user?.let { listUser.add(it) }
                }
                setUpRecyclerView(listUser, context)
            }
    }

}