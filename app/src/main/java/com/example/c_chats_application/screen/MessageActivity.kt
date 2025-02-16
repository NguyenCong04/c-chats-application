package com.example.c_chats_application.screen

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.c_chats_application.R
import com.example.c_chats_application.config.COMMON
import com.example.c_chats_application.databinding.LayoutTextingMessageBinding
import com.google.firebase.firestore.FirebaseFirestore

class MessageActivity : AppCompatActivity() {

    private val TAG = "ZZMessageActivityZZ"
    private lateinit var binding: LayoutTextingMessageBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LayoutTextingMessageBinding.inflate(layoutInflater)

        //back
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            finish()
        }

        //get Intent
        val idUserStatus = intent.getStringExtra("idUserStatus") ?: ""

        uiHiShowMicAndImage()
        fetchUser(idUserStatus)

        setContentView(binding.root)
    }

    private fun fetchUser(idUserStatus: String?) {
        if (idUserStatus != null) {
            db.collection("user").document(idUserStatus)
                .addSnapshotListener { value, error ->
                    if (error != null) return@addSnapshotListener

                    if (value != null) {
                        val name = value.getString("name").toString()
                        val image = value.getString("image").toString()
                        val status = value.getString("status").toString()
                        if (!isDestroyed) {
                            Glide.with(this)
                                .load(image)
                                .placeholder(R.drawable.icon_avata_default)
                                .into(binding.ivAvatarItemTinNhan)
                        }
                        binding.tvNameUser.text = name
                        binding.tvStatusUser.text = status
                        val backgroundDrawable =
                            binding.vTrangThaiUser.background as GradientDrawable
                        backgroundDrawable.setColor(if (status == COMMON.statusOnline) Color.GREEN else Color.GRAY)
                    }

                }
        }

    }

    private fun uiHiShowMicAndImage() {
        binding.edtSoanNhanTin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString() == "") {
                    binding.layoutMicAndImage.visibility = View.VISIBLE
                    binding.layoutBtnSend.visibility = View.GONE
                } else {
                    binding.layoutMicAndImage.visibility = View.GONE
                    binding.layoutBtnSend.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

    }

}
