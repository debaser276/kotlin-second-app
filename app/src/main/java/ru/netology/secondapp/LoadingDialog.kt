package ru.netology.secondapp

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater

class LoadingDialog(context: Context): Dialog(context) {
    init {
        setContentView(LayoutInflater.from(context).inflate(R.layout.dialog_loading, null))
        setCancelable(false)
    }
}