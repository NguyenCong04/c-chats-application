package com.example.c_chats_application.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.c_chats_application.R
import com.example.c_chats_application.databinding.ItemUserBinding
import com.example.c_chats_application.model.UserModel

class ListUserAdapter(private val listUser: List<UserModel>) :
    RecyclerView.Adapter<ListUserAdapter.UserAdapterViewHolder>() {
    private lateinit var binding: ItemUserBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapterViewHolder {
        binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return UserAdapterViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listUser.size
    }

    override fun onBindViewHolder(holder: UserAdapterViewHolder, position: Int) {
        val item = listUser[position]

        holder.bin(item, binding)

    }

    class UserAdapterViewHolder(itemView: ItemUserBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        fun bin(user: UserModel, itemView: ItemUserBinding) {
            itemView.tvName.text = user.name
            val context = itemView.root.context
            if (context is Activity && !context.isDestroyed) {
                Glide.with(context)
                    .load(user.image)
                    .placeholder(R.drawable.icon_avata_default)
                    .into(itemView.ivAvatar)
            }
        }

    }

}