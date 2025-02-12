package com.example.c_chats_application.screen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.c_chats_application.databinding.LayoutWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: LayoutWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LayoutWelcomeBinding.inflate(layoutInflater)

        binding.btnStart.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }



        setContentView(binding.root)
    }

}