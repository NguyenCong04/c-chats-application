package com.example.c_chats_application.model

data class ChatModel(
    val chatId: String? = null,  // ID của cuộc trò chuyện
    val lastMessage: String? = null,  // Tin nhắn cuối cùng
    val lastMessageTimestamp: Long? = null,  // Thời gian gửi tin nhắn cuối
    val unreadMessages: Map<String, Int> = emptyMap(),  // Số lượng tin nhắn chưa đọc của từng người dùng
    val participants: List<String> = emptyList(),  // Danh sách ID người tham gia
    val isGroup: Boolean = false,  // Đánh dấu cuộc trò chuyện là nhóm hay 1-1
    val groupName: String? = null,  // Tên nhóm (nếu là chat nhóm)
    val groupAvatar: String? = null  // Ảnh đại diện nhóm (nếu là chat nhóm)
)