package com.example.c_chats_application.screen

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.c_chats_application.MainActivity
import com.example.c_chats_application.R
import com.example.c_chats_application.config.COMMON
import com.example.c_chats_application.databinding.LayoutLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: LayoutLoginBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
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

        //start main
        binding.btnLogin.setOnClickListener {
            logIn()
        }

    }

    private fun logIn() {
        val email = binding.edEmail.text.toString().trim()
        val password = binding.edPassword.text.toString().trim()
        if (checkLogin(email, password)) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    Toast.makeText(this, "Login successfully", Toast.LENGTH_SHORT).show()
                    db.collection("user").document(auth.currentUser?.uid.toString())
                        .update("status", COMMON.statusOnline).addOnFailureListener {
                            Log.e(
                                TAG,
                                "logIn: $it -- ${it.message}"
                            )
                        }
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    try {
                        throw it
                    } catch (e: FirebaseAuthInvalidUserException) {
                        Toast.makeText(this, "Email or password is incorrect", Toast.LENGTH_SHORT)
                            .show()
                    } catch (e: FirebaseAuthInvalidUserException) {
                        Toast.makeText(this, "Email or password is incorrect", Toast.LENGTH_SHORT)
                            .show()
                    } catch (e: Exception) {
                        Toast.makeText(this, "Error ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun checkLogin(email: String, password: String): Boolean {
        if (
            email.isBlank() ||
            password.isBlank()
        ) {
            Toast.makeText(this, "Please enter information", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
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