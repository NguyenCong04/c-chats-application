package com.example.c_chats_application.model

data class MessageModel(
    val messageId: String? = null,  // ID của tin nhắn
    val senderId: String? = null,  // ID người gửi
    val text: String? = null,  // Nội dung tin nhắn (nếu có)
    val mediaUrl: String? = null,  // Link ảnh/video (nếu có)
    val voiceUrl: String? = null,  // Link tin nhắn thoại (nếu có)
    val messageType: String = "text",  // Loại tin nhắn: "text", "image", "video", "voice"
    val timestamp: Long? = null,  // Thời gian gửi tin nhắn
    val readStatus: Map<String, Boolean> = emptyMap()  // Trạng thái đọc tin của từng người
)
