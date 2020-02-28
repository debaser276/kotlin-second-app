package ru.netology.secondapp

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView

class LoadingDialog(context: Context): Dialog(context) {
    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null)
        val tvMessage = view.findViewById<TextView>(R.id.tv_message)
        tvMessage.text = "${context.resources.getString(R.string.please_wait)}"
        setContentView(view)
        setCancelable(false)
    }
}