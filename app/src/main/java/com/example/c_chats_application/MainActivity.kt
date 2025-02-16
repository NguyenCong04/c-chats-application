package com.example.c_chats_application

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.c_chats_application.databinding.ActivityMainBinding
import com.example.c_chats_application.fragment.HomeFragment
import com.example.c_chats_application.fragment.ListUserFragment
import com.example.c_chats_application.fragment.SettingFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
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

        setContentView(binding.root)

    }
}
