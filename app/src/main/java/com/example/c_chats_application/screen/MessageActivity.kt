package com.example.c_chats_application.screen

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.c_chats_application.R
import com.example.c_chats_application.adapter.MessageAdapter
import com.example.c_chats_application.config.COMMON
import com.example.c_chats_application.databinding.LayoutTextingMessageBinding
import com.example.c_chats_application.model.ChatModel
import com.example.c_chats_application.model.MessageModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import org.json.JSONObject

class MessageActivity : AppCompatActivity() {

    private val TAG = "ZZMessageActivityZZ"
    private lateinit var binding: LayoutTextingMessageBinding
    private val db = FirebaseFirestore.getInstance()
    private val chatsCollection = db.collection("chats")
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LayoutTextingMessageBinding.inflate(layoutInflater)

        //back
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            finish()
        }

        //get Intent
        val idUserStatus = intent.getStringExtra("idUserStatus") ?: ""
        val idUserCurrent = auth.currentUser?.uid ?: ""

        uiHiShowMicAndImage()
        fetchUser(idUserStatus)

        binding.layoutBtnSend.setOnClickListener {
            val text = binding.edtSoanNhanTin.text.toString().trim()
            sendTextMessage(idUserCurrent, idUserStatus, text) {
                if (it) {
                    Log.e(TAG, "onCreate: send successfully $idUserStatus")
                    binding.edtSoanNhanTin.text.clear()
                } else {
                    Log.e(TAG, "onCreate: send failed $idUserStatus")
                    binding.edtSoanNhanTin.text.clear()
                }
            }
        }
        getChatIdForOneToOneChat(idUserCurrent, idUserStatus) { chatId ->
            if (chatId != null) {
                fetchMessages(chatId) { messages ->
                    val adapter = MessageAdapter(messages, idUserCurrent)
                    binding.rcvChat.layoutManager = LinearLayoutManager(this).apply {
                        stackFromEnd = true // Tự động cuộn xuống cuối danh sách
                    }
                    binding.rcvChat.adapter = adapter
                }
            } else {
                Log.d("Chat", "Chưa có cuộc trò chuyện giữa hai người này.")
            }
        }

        setContentView(binding.root)
    }

    private fun fetchUser(idUserStatus: String?) {
        if (idUserStatus != null) {
            db.collection("user").document(idUserStatus)
                .addSnapshotListener { value, error ->
                    if (error != null) return@addSnapshotListener

                    if (value != null) {
                        val name = value.getString("name").toString()
                        val image = value.getString("image").toString()
                        val status = value.getString("status").toString()
                        if (!isDestroyed) {
                            Glide.with(this)
                                .load(image)
                                .placeholder(R.drawable.icon_avata_default)
                                .into(binding.ivAvatarItemTinNhan)
                        }
                        binding.tvNameUser.text = name
                        binding.tvStatusUser.text = status
                        val backgroundDrawable =
                            binding.vTrangThaiUser.background as GradientDrawable
                        backgroundDrawable.setColor(if (status == COMMON.statusOnline) Color.GREEN else Color.GRAY)
                    }

                }
        }

    }
    private fun sendTextMessage(
        senderId: String,
        receiverId: String,
        messageText: String,
        callback: (Boolean) -> Unit
    ) {
        val chatParticipants =
            listOf(senderId, receiverId).sorted() // Đảm bảo ID luôn có thứ tự cố định
        val chatQuery = chatsCollection.whereEqualTo("participants", chatParticipants)

        chatQuery.get().addOnSuccessListener { querySnapshot ->
            val chatId = if (querySnapshot.isEmpty) {
                // Nếu chưa có cuộc trò chuyện, tạo chat mới
                val newChatId = chatsCollection.document().id
                val newChat = ChatModel(
                    chatId = newChatId,
                    lastMessage = messageText,
                    lastMessageTimestamp = System.currentTimeMillis(),
                    unreadMessages = mapOf(receiverId to 1), // Người nhận có 1 tin chưa đọc
                    participants = chatParticipants,
                    isGroup = false
                )
                chatsCollection.document(newChatId).set(newChat)
                newChatId
            } else {
                querySnapshot.documents[0].id
            }

            // Gửi tin nhắn
            val messageId = chatsCollection.document().id
            val message = MessageModel(
                messageId = messageId,
                senderId = senderId,
                text = messageText,
                messageType = "text",
                timestamp = System.currentTimeMillis(),
                readStatus = mapOf(
                    senderId to true,
                    receiverId to false
                ) // Người gửi đã đọc, người nhận chưa
            )

            chatsCollection.document(chatId).collection("messages").document(messageId)
                .set(message)
                .addOnSuccessListener {
                    // Cập nhật thông tin cuộc trò chuyện
                    chatsCollection.document(chatId).update(
                        mapOf(
                            "lastMessage" to messageText,
                            "lastMessageTimestamp" to System.currentTimeMillis(),
                            "unreadMessages.$receiverId" to (querySnapshot.documents.firstOrNull()
                                ?.get("unreadMessages.$receiverId") as? Int ?: 0) + 1
                        )
                    )
                    // Gửi thông báo cho người nhận
                    sendNotification(receiverId, messageText, chatId)
                    callback(true)
                }
                .addOnFailureListener {
                    callback(false)
                }
        }.addOnFailureListener {
            callback(false)
        }
    }

    fun fetchMessages(chatId: String, onMessagesFetched: (List<MessageModel>) -> Unit) {
        chatsCollection.document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING) // Lấy tin nhắn theo thứ tự thời gian
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    Log.e("Chat", "Lỗi khi lấy tin nhắn: ${error.message}")
                    return@addSnapshotListener
                }

                val messages =
                    querySnapshot?.documents?.mapNotNull { it.toObject(MessageModel::class.java) }
                messages?.let { onMessagesFetched(it) }
            }
    }

    fun getChatIdForOneToOneChat(user1: String, user2: String, callback: (String?) -> Unit) {
        val chatParticipants = listOf(user1, user2).sorted() // Đảm bảo thứ tự
        chatsCollection
            .whereEqualTo("participants", chatParticipants) // Tìm chat giữa 2 người
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val chatId = querySnapshot.documents.first().id
                    callback(chatId)
                } else {
                    callback(null) // Chưa có cuộc trò chuyện
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun sendNotification(receiverId: String, messageText: String, chatId: String) {
        val usersCollection = FirebaseFirestore.getInstance().collection("user")

        // Lấy FCM token của người nhận
        usersCollection.document(receiverId).get()
            .addOnSuccessListener { document ->
                val token = document.getString("fcmToken")
                if (token != null) {
                    val json = JSONObject()
                    val notification = JSONObject()
                    val data = JSONObject()

                    notification.put("title", "Tin nhắn mới")
                    notification.put("body", messageText)

                    data.put("chatId", chatId)

                    json.put("message", JSONObject().apply {
                        put("token", token)
                        put("notification", notification)
                        put("data", data)
                    })

                    val client = OkHttpClient()
                    val requestBody = json.toString()
                        .toRequestBody("application/json; charset=utf-8".toMediaType())
                    val request = Request.Builder()
                        .url("https://fcm.googleapis.com/v1/projects/project-app-65c58/messages:send")
                        .post(requestBody)
                        .header("Authorization", "Bearer ${COMMON.accessToken}") // Thay thế với Bearer token
                        .header("Content-Type", "application/json")
                        .build()

                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            Log.e("FCM", "Lỗi khi gửi thông báo", e)
                        }

                        override fun onResponse(call: Call, response: Response) {
                            Log.d("FCM", "Gửi thông báo thành công: ${response.body?.string()}")
                        }
                    })
                }
            }
    }





    private fun uiHiShowMicAndImage() {
        binding.edtSoanNhanTin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString() == "") {
                    binding.layoutMicAndImage.visibility = View.VISIBLE
                    binding.layoutBtnSend.visibility = View.GONE
                } else {
                    binding.layoutMicAndImage.visibility = View.GONE
                    binding.layoutBtnSend.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

    }

}
