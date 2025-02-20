package com.example.c_chats_application.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.example.c_chats_application.R
import com.example.c_chats_application.screen.MessageActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "ZZMyFirebaseMessagingServiceZZ"
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val senderId = remoteMessage.data["senderId"]
        remoteMessage.notification?.let {
            if (senderId != null) {
                showNotificationReply(it.title ?: "Tin nhắn mới", it.body ?: "", senderId)
            }
        }
        remoteMessage.data.let {
            Log.e(TAG, "onMessageReceived:receiverId ${it["receiverId"]}")
            Log.e(TAG, "onMessageReceived:chatId ${it["chatId"]}")
        }
    }

    private fun showNotificationReply(title: String, message: String, receiverId: String) {
        val channelId = "chat_notifications"

        // Intent để mở MessageActivity khi bấm vào thông báo
        val intent = Intent(this, MessageActivity::class.java)
        intent.putExtra("idUserStatus", receiverId)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 📝 Tạo Action Reply (Trả lời tin nhắn)
        val remoteInput = RemoteInput.Builder("key_text_reply")
            .setLabel("Nhập tin nhắn...")
            .build()

        val replyIntent = Intent(this, ReplyReceiver::class.java).apply {
            putExtra("idUserStatus", receiverId) // Đặt giá trị đúng
        }
        val replyPendingIntent = PendingIntent.getBroadcast(
            this, 0, replyIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        val replyAction = NotificationCompat.Action.Builder(
            R.drawable.icon_reply_message, "Trả lời", replyPendingIntent
        ).addRemoteInput(remoteInput).build()

        // 🛎️ Xây dựng thông báo
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.icon_avata_default)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .addAction(replyAction) // ✅ Thêm nút "Trả lời"

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Chat Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }


}
