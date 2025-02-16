package com.example.c_chats_application.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.c_chats_application.databinding.FragmentLayoutHomeBinding

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentLayoutHomeBinding
    private var TAG = "ZZHomeFragmentZZ"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLayoutHomeBinding.inflate(inflater, container, false)
        Log.e(TAG, "onCreateView: Home")

        return binding.root
    }

}