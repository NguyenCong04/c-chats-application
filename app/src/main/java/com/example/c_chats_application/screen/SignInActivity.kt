package com.example.c_chats_application.screen

import android.os.Bundle
import android.text.InputType
import androidx.appcompat.app.AppCompatActivity
import com.example.c_chats_application.R
import com.example.c_chats_application.databinding.LayoutSignInBinding

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: LayoutSignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LayoutSignInBinding.inflate(layoutInflater)

        showHidePassword()
        startAc()

        setContentView(binding.root)

    }

    private fun startAc() {

        binding.tvLogin.setOnClickListener {
            onBack()
        }
        binding.btnBack.setOnClickListener {
            onBack()
        }

    }

    private fun onBack() {
        onBackPressedDispatcher.onBackPressed()
        finish()
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
        binding.layoutEdConfirmPassword.setEndIconOnClickListener {

            if (binding.edConfirmPassword.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                binding.edConfirmPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.layoutEdConfirmPassword.setEndIconDrawable(R.drawable.show_password)
            } else if (binding.edConfirmPassword.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                binding.edConfirmPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.layoutEdConfirmPassword.setEndIconDrawable(R.drawable.hide_password)
            }
            binding.edConfirmPassword.setSelection(binding.edConfirmPassword.text.length)

        }

    }


}