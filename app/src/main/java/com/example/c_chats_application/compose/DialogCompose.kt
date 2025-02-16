package com.example.c_chats_application.compose

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.example.c_chats_application.databinding.LayoutDialogBinding

fun DialogCompose(
    title: String,
    msg: String,
    context: Context,
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {

    val dialog = Dialog(context)
    val layoutInflater = LayoutInflater.from(context)
    val binding = LayoutDialogBinding.inflate(layoutInflater)
    dialog.setContentView(binding.root)
    binding.tvMsgDialog.text = msg
    binding.tvTitleDialog.text = title
    binding.btnCancel.setOnClickListener {
        onCancel()
        dialog.dismiss()
    }
    binding.btnConfirm.setOnClickListener {
        onConfirm()
        dialog.dismiss()
    }
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    dialog.show()

}