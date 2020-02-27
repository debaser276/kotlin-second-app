package ru.netology.secondapp

import android.content.Context
import android.widget.Toast
import java.util.regex.Pattern

fun isValid(password: String) = Pattern
    .compile("(?=.*[A-Z])[a-zA-Z0-9]{6,}")
    .matcher(password)
    .matches()

fun Context.toast(res: Int) = Toast.makeText(this, this.resources.getString(res), Toast.LENGTH_SHORT).show()