package com.example.c_chats_application.screen

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import androidx.appcompat.app.AppCompatActivity
import com.example.c_chats_application.R
import com.example.c_chats_application.databinding.LayoutLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: LayoutLoginBinding
    private var TAG = "ZZLoginActivityZZ"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LayoutLoginBinding.inflate(layoutInflater)

        showHidePassword()
        startActivitySignInAndMain()

        setContentView(binding.root)
    }

    private fun startActivitySignInAndMain() {

        //start SignIn
        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

    }

    private fun showHidePassword() {

        binding.layoutEdPassword.setEndIconOnClickListener {

            if (binding.edPassword.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                binding.edPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.layoutEdPassword.setEndIconDrawable(R.drawable.show_password)
            } else if (binding.edPassword.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                binding.edPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.layoutEdPassword.setEndIconDrawable(R.drawable.hide_password)
            }
            binding.edPassword.setSelection(binding.edPassword.text.length)

        }

    }

}