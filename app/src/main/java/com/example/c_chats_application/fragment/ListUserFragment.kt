package com.example.c_chats_application.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.c_chats_application.adapter.ListUserAdapter
import com.example.c_chats_application.databinding.FragmentLayoutListUserBinding
import com.example.c_chats_application.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ListUserFragment : Fragment() {
    private lateinit var binding: FragmentLayoutListUserBinding
    private var TAG = "ZZListUserFragmentZZ"
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var userList = mutableListOf<UserModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLayoutListUserBinding.inflate(inflater, container, false)
        Log.e(TAG, "onCreateView: List")

        fetchUser()
        return binding.root
    }

    private fun setUpRecyclerView(userList: MutableList<UserModel>, context: Context?) {
        val userAdapter = ListUserAdapter(userList)
        val layoutManager = GridLayoutManager(context, 2)
        binding.rcvListUser.layoutManager = layoutManager
        binding.rcvListUser.adapter = userAdapter
    }

    private fun fetchUser() {
        val userId = auth.currentUser?.uid.toString()
        db.collection("user")
            .whereNotEqualTo("idUser", userId) // Loại bỏ userId khỏi danh sách
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e(TAG, "Lỗi khi lấy dữ liệu: ", error)
                    return@addSnapshotListener
                }
                userList.clear()
                value?.documents?.forEach { document ->
                    val user = document.toObject(UserModel::class.java)
                    Log.d(TAG, "fetchUser: $user")
                    user?.let { userList.add(it) }
                }
                setUpRecyclerView(userList, context)
            }
    }
}