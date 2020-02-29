package ru.netology.secondapp

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView

class LoadingDialog(context: Context): AlertDialog(context) {
    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null)
        val tvMessage = view.findViewById<TextView>(R.id.tv_message)
        tvMessage.text = "${context.resources.getString(R.string.please_wait)}"
        setView(view)
        setCancelable(false)
    }
}