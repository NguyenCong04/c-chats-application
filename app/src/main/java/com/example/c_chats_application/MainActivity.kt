package com.example.c_chats_application

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.c_chats_application.databinding.ActivityMainBinding
import com.example.c_chats_application.fragment.HomeFragment
import com.example.c_chats_application.fragment.ListUserFragment
import com.example.c_chats_application.fragment.SettingFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, HomeFragment()) // Load HomeFragment
                .commit()
        }
        binding.bottomNav.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = HomeFragment()

            when (item.itemId) {
                R.id.item_home -> selectedFragment = HomeFragment()
                R.id.item_search -> selectedFragment = ListUserFragment()
                R.id.item_profile -> selectedFragment = SettingFragment()
            }

            // Replace the fragment
            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, selectedFragment)
                    .commit()
            }

            true
        }
        // Chọn mặc định tab Home
        binding.bottomNav.selectedItemId = R.id.item_home

        //update token user
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val newToken = task.result
                    val currentUserId = auth.currentUser?.uid
                    if (currentUserId != null) {
                        updateUserToken(currentUserId, newToken)
                    }
                }
            }



        setContentView(binding.root)

    }

    fun updateUserToken(userId: String, token: String) {
        val userRef = FirebaseFirestore.getInstance().collection("user").document(userId)
        userRef.update("fcmToken", token)
            .addOnSuccessListener { Log.d("FCM", "Token cập nhật thành công!") }
            .addOnFailureListener { Log.e("FCM", "Lỗi khi cập nhật token", it) }
    }



}
