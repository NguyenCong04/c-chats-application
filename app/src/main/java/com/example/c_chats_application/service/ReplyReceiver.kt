package com.example.c_chats_application.service

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.example.c_chats_application.R
import com.example.c_chats_application.model.ChatModel
import com.example.c_chats_application.model.MessageModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class ReplyReceiver : BroadcastReceiver() {
    private val TAG = "ZZReplyReceiverZZ"
    private val db = FirebaseFirestore.getInstance()
    private val chatsCollection = db.collection("chats")
    private val auth = FirebaseAuth.getInstance()
    override fun onReceive(context: Context?, intent: Intent?) {
        val remoteInput = intent?.let { RemoteInput.getResultsFromIntent(it) }
        if (remoteInput != null) {
            val replyText = remoteInput.getCharSequence("key_text_reply").toString()
            Log.d("ReplyReceiver", "Người dùng đã trả lời: $replyText")

            // 🔥 Gửi tin nhắn đến server hoặc Firebase ở đây
            val idUserStatus = intent.getStringExtra("idUserStatus") ?: ""
            Log.e(TAG, "onReceive: $idUserStatus")
            val userIdCurrent = auth.currentUser?.uid ?: ""
            sendTextMessage(userIdCurrent, idUserStatus, replyText) {
                if (it) {
                    Log.e(TAG, "onReceive: send thanh cong")
                }else{
                    Log.e(TAG, "onReceive: send error")
                }
            }
            // Cập nhật thông báo để ẩn ô nhập sau khi trả lời xong
            val notificationManager =
                context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notification = NotificationCompat.Builder(context, "chat_notifications")
                .setSmallIcon(R.drawable.icon_avata_default)
                .setContentText("Bạn đã trả lời: $replyText")
                .build()

            notificationManager.notify(0, notification)
        }else{
            Log.e(TAG, "onReceive: kkkkk" )
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
                    sendNotification(receiverId, messageText, chatId, senderId)
                    callback(true)
                }
                .addOnFailureListener {
                    callback(false)
                }
        }.addOnFailureListener {
            callback(false)
        }
    }

    fun sendNotification(
        receiverId: String,
        messageText: String,
        chatId: String,
        senderId: String
    ) {
        val usersCollection = FirebaseFirestore.getInstance().collection("user")

        // Lấy FCM token của người nhận
        usersCollection.document(receiverId).get()
            .addOnSuccessListener { document ->
                val token = document.getString("fcmToken")
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://server-access-token.onrender.com/access-token")
                    .get()
                    .header("Content-Type", "application/json")
                    .build()
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.e("FCM", "Lỗi khi gửi thông báo", e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        Log.d("FCM", "Gửi thông báo thành công: ${response.body.toString()}")
                        response.use {
                            if (!response.isSuccessful) {
                                Log.e("FCM", "Lỗi HTTP: ${response.code}")
                                return
                            }

                            val responseBody =
                                response.body?.string() ?: "{}"  // Nếu body null, dùng "{}"
                            try {
                                val jsonObject =
                                    JSONObject(responseBody)  // Chuyển response thành JSON
                                val accessToken =
                                    jsonObject.getString("accessToken") // Lấy accessToken
                                Log.d("FCM", "Access Token: $accessToken")
                                if (token != null) {
                                    val json = JSONObject()
                                    val notification = JSONObject()
                                    val data = JSONObject()

                                    notification.put("title", "Tin nhắn mới")
                                    notification.put("body", messageText)

                                    data.put("senderId", senderId)
                                    data.put("chatId", chatId)

                                    json.put("message", JSONObject().apply {
                                        put("token", token)
                                        put("notification", notification)
                                        put("data", data)
                                    })
                                    val requestBody = json.toString()
                                        .toRequestBody("application/json; charset=utf-8".toMediaType())
                                    val requestFCM = Request.Builder()
                                        .url("https://fcm.googleapis.com/v1/projects/project-app-65c58/messages:send")
                                        .post(requestBody)
                                        .header(
                                            "Authorization",
                                            "Bearer $accessToken"
                                        ) // Thay thế với Bearer token
                                        .header("Content-Type", "application/json")
                                        .build()

                                    client.newCall(requestFCM).enqueue(object : Callback {
                                        override fun onFailure(call: Call, e: IOException) {
                                            Log.e("FCM", "Lỗi khi gửi thông báo", e)
                                        }

                                        override fun onResponse(call: Call, response: Response) {
                                            Log.d(
                                                "FCM",
                                                "Gửi thông báo thành công: ${response.body?.string()}"
                                            )
                                        }
                                    })
                                } else {
                                    Log.e(TAG, "onResponse: error")
                                }
                            } catch (e: Exception) {
                                Log.e("FCM", "Lỗi parse JSON", e)
                            }
                        }

                    }
                })
            }
    }


}
