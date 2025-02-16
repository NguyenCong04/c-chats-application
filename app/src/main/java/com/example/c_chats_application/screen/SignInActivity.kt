package com.example.c_chats_application.screen

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.c_chats_application.MainActivity
import com.example.c_chats_application.R
import com.example.c_chats_application.config.COMMON
import com.example.c_chats_application.databinding.LayoutSignInBinding
import com.example.c_chats_application.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: LayoutSignInBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "ZZSignInActivityZZ"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LayoutSignInBinding.inflate(layoutInflater)

        showHidePassword()
        startAc()

        binding.btnSigUp.setOnClickListener {
            sigUp()
        }

        setContentView(binding.root)

    }

    private fun sigUp() {
        val fullName = binding.edFullName.text.toString().trim()
        val email = binding.edEmail.text.toString().trim()
        val password = binding.edPassword.text.toString().trim()
        val confirmPassword = binding.edConfirmPassword.text.toString().trim()

        if (checkLogin(fullName, email, password, confirmPassword)) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                        val user = UserModel(
                            userId,
                            fullName,
                            email,
                            COMMON.urlImageDefault,
                            COMMON.statusOnline
                        )
                        Toast.makeText(this, "Register successfully", Toast.LENGTH_SHORT).show()
                        db.collection("user").document(userId).set(user)
                        login(email, password)
                    }
                }
                .addOnFailureListener {
                    Log.e(TAG, "sigUp: ${it.message.toString()}")
                    Log.e(TAG, "sigUp: $it")
                    try {
                        throw it
                    } catch (e: FirebaseAuthUserCollisionException) {
                        Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(this, "Error ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
    }

    private fun checkLogin(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {

        if (
            fullName.isBlank() ||
            email.isBlank() ||
            password.isBlank() ||
            confirmPassword.isBlank()
        ) {
            Toast.makeText(this, "Please enter information", Toast.LENGTH_SHORT).show()
            return false
        }
        // Kiểm tra độ dài mật khẩu
        if (password.length < 8) {
            Toast.makeText(this, "Password must be greater than 8 characters", Toast.LENGTH_SHORT)
                .show()
            return false
        }

        // Kiểm tra mật khẩu có ít nhất 1 chữ hoa, 1 chữ thường, 1 số và 1 ký tự đặc biệt
        val passwordPattern = Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}$")
        if (!password.matches(passwordPattern)) {
            Toast.makeText(
                this,
                "Password must have at least 1 uppercase letter, 1 lowercase letter, 1 number and 1 special character",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        // Kiểm tra mật khẩu nhập lại
        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }
        // Kiểm tra email hợp lệ
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email invalid", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
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