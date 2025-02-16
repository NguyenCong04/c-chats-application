package com.example.c_chats_application.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.c_chats_application.databinding.FragmentLayoutListUserBinding

class ListUserFragment : Fragment() {
    private lateinit var binding: FragmentLayoutListUserBinding
    private var TAG = "ZZListUserFragmentZZ"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLayoutListUserBinding.inflate(inflater, container, false)
        Log.e(TAG, "onCreateView: List")

        return binding.root
    }
}