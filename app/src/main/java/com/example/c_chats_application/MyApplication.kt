package com.example.c_chats_application

import android.app.Application
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class MyApplication : Application(), DefaultLifecycleObserver {

    override fun onCreate() {
        super<Application>.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        setOnlineStatus(true) // Khi app mở, đặt trạng thái online
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        setOnlineStatus(false) // Khi app vào background, đặt trạng thái offline
    }

    private fun setOnlineStatus(isOnline: Boolean) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userStatusRef = FirebaseFirestore.getInstance().collection("user").document(userId)

        if (isOnline) {

            userStatusRef.update(
                "status", "online"
            )
                .addOnFailureListener { Log.e("StatusUpdate", "Failed to update status") }
        } else {
            userStatusRef.update(
                "status", "offline",
                "lastSeen", FieldValue.serverTimestamp()
            )
                .addOnFailureListener { Log.e("StatusUpdate", "Failed to update status") }
        }

    }
}