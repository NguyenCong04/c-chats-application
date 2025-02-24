package com.example.c_chats_application.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.c_chats_application.databinding.ItemTinNhanLeftBinding
import com.example.c_chats_application.databinding.ItemTinNhanRightBinding
import com.example.c_chats_application.model.MessageModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageAdapter(private val messages: List<MessageModel>, private val currentUserId: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var TAG = "ZZMessageAdapterZZ"

    companion object {
        private const val VIEW_TYPE_SEND = 1
        private const val VIEW_TYPE_RECEIVE = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SEND) {
            val view =
                ItemTinNhanRightBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            Log.d(TAG, "onCreateViewHolder: viewType $viewType")
            SendViewHolder(view)
        } else {
            val view =
                ItemTinNhanLeftBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            Log.d(TAG, "onCreateViewHolder: viewType else $viewType")
            ReceiveViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        Log.d(TAG, "getItemViewType: $position")
        return if (messages[position].senderId == currentUserId) VIEW_TYPE_SEND else VIEW_TYPE_RECEIVE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        Log.d(TAG, "onBindViewHolder:message $message")
        if (holder is SendViewHolder) {
            holder.bind(message)
        } else if (holder is ReceiveViewHolder) {
            holder.bind(message)
        }
    }

    class SendViewHolder(itemView: ItemTinNhanRightBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        private val textMessageSend = itemView.tvTinNhanRight
        private val tvTimeRight = itemView.tvTimeRight
        fun bind(message: MessageModel) {
            val time = convertTimestampToTime(message.timestamp!!.toLong())
            textMessageSend.text = message.text
            tvTimeRight.text = time

        }

        fun convertTimestampToTime(timestamp: Long): String {
            // Chuyển đổi timestamp thành đối tượng Date
            val date = Date(timestamp)

            // Đặt định dạng thời gian bạn muốn hiển thị
            val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

            // Chuyển đổi Date thành chuỗi theo định dạng đã chỉ định
            return dateFormat.format(date)
        }
    }

    class ReceiveViewHolder(itemView: ItemTinNhanLeftBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        private val textMessageReceive = itemView.tvTinNhanLeft
        private val tvTimeLeft = itemView.tvTimeLeft
        fun bind(message: MessageModel) {
            val time = convertTimestampToTime(message.timestamp!!.toLong())
            textMessageReceive.text = message.text
            tvTimeLeft.text = time
        }

        fun convertTimestampToTime(timestamp: Long): String {
            // Chuyển đổi timestamp thành đối tượng Date
            val date = Date(timestamp)

            // Đặt định dạng thời gian bạn muốn hiển thị
            val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

            // Chuyển đổi Date thành chuỗi theo định dạng đã chỉ định
            return dateFormat.format(date)
        }
    }

}