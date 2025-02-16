package com.example.c_chats_application.adapter

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.c_chats_application.R
import com.example.c_chats_application.config.COMMON
import com.example.c_chats_application.databinding.ItemUserStatusMessengerBinding
import com.example.c_chats_application.model.UserModel
import com.example.c_chats_application.screen.MessageActivity

class UserStatusAdapter(private val users: List<UserModel>) :
    RecyclerView.Adapter<UserStatusAdapter.UserStatusAdapterViewHolder>() {
    private lateinit var binding: ItemUserStatusMessengerBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserStatusAdapterViewHolder {
        binding = ItemUserStatusMessengerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserStatusAdapterViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: UserStatusAdapterViewHolder, position: Int) {
        val user = users[position]
        holder.bin(user, binding)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, MessageActivity::class.java)
            intent.putExtra("idUserStatus", user.idUser)
            holder.itemView.context.startActivity(intent)
        }

    }

    class UserStatusAdapterViewHolder(itemUserBinding: ItemUserStatusMessengerBinding) :
        RecyclerView.ViewHolder(itemUserBinding.root) {
        fun bin(user: UserModel, binding: ItemUserStatusMessengerBinding) {
            Glide.with(binding.root.context)
                .load(user.image)
                .placeholder(R.drawable.icon_avata_default)
                .circleCrop()
                .into(binding.ivAvatarItemTinNhan)
            val statusDrawable = binding.vTrangThaiUser.background as GradientDrawable
            statusDrawable.setColor(if (user.status == COMMON.statusOnline) Color.GREEN else Color.GRAY)
//            binding.tvNameUserStatus.text = user.name
            val fullName = user.name.trim() // Loại bỏ khoảng trắng đầu & cuối
            val nameParts = fullName.split(" ") // Tách thành danh sách các từ

            // Nếu có nhiều từ, lấy từ cuối cùng; nếu chỉ có một từ, giữ nguyên
            val displayName = nameParts.lastOrNull() ?: fullName

            binding.tvNameUserStatus.text = displayName
        }
    }
}