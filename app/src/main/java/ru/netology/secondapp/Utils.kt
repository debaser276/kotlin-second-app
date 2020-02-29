package ru.netology.secondapp

import android.content.Context
import android.widget.Toast
import java.util.regex.Pattern

private val pattern by lazy(LazyThreadSafetyMode.NONE) {
    Pattern.compile("(?=.*[A-Z])[a-zA-Z0-9]{6,}")
}

fun isValid(password: String) = pattern
    .matcher(password)
    .matches()

fun Context.toast(res: Int) = Toast.makeText(this, this.resources.getString(res), Toast.LENGTH_SHORT).show()

fun Context.getUserId(): Int = this.getSharedPreferences(API_SHARED_file, Context.MODE_PRIVATE).getInt(
    AUTHENTICATED_ID, 0)

object TimeConverter {
    fun convertSeconds(seconds: Long): String {

        val inMinute = 60 // 60 seconds in minute
        val inHour = 3_600 // 60 minutes in hour
        val inDay = 86_400 // 24 hours in day
        val inMonth = 2_592_000 // 30 days in month
        val inYear = 31_104_000 // 12 months in year

        fun convertToYears(seconds: Long): String {
            val years = seconds / inYear
            return if (years > 4)
                "Несколько лет "
            else
                years.toString() + when {
                    years == 1L -> " год "
                    else -> " года "
                }
        }

        fun convertToMonths(seconds: Long): String {
            val months = seconds / inMonth
            return months.toString() + when (months) {
                1L -> " месяц "
                in 2..4 -> " месяца "
                else -> " месяцев "
            }
        }

        fun convertToDays(seconds: Long): String {
            val days = seconds / inDay
            return days.toString() + when {
                days in 11..14 -> " дней "
                days % 10 == 1L -> " день "
                days % 10 in 2..4 -> " дня "
                else -> " дней "
            }
        }

        fun convertToHours(seconds: Long): String {
            val hours = seconds / inHour
            return hours.toString() + when {
                hours in 11..14 -> " часов "
                hours % 10 == 1L -> " час "
                hours % 10 in 2..4 -> " часа "
                else -> " часов "
            }
        }

        fun convertToMinutes(seconds: Long): String {
            val minutes = seconds / inMinute
            return minutes.toString() + when {
                minutes in 11..14 -> " минут "
                minutes % 10 == 1L -> " минуту "
                minutes % 10 in 2..4 -> " минуты "
                else -> " минут "
            }
        }

        return when {
            seconds / inYear > 0 -> convertToYears(seconds)
            seconds / inMonth > 0 -> convertToMonths(seconds)
            seconds / inDay > 0 -> convertToDays(seconds)
            seconds / inHour > 0 -> convertToHours(seconds)
            seconds / inMinute > 0 -> convertToMinutes(seconds)
            else -> "менее минуты "
        } + "назад"
    }
}