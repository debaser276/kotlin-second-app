package ru.netology.secondapp

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.core.content.edit
import java.util.*
import java.util.regex.Pattern

private val pattern by lazy(LazyThreadSafetyMode.NONE) {
    Pattern.compile("(?=.*[A-Z])[a-zA-Z0-9]{6,}")
}

fun isValid(password: String) = pattern
    .matcher(password)
    .matches()

fun Context.toast(res: Int) = Toast.makeText(this, this.resources.getString(res), Toast.LENGTH_SHORT).show()

fun Context.getUserId(): Int = this.getSharedPreferences(API_SHARED_FILE, Context.MODE_PRIVATE).getInt(
    AUTHENTICATED_ID, 0)

fun isFirstTime(context: Context) =
    context.getSharedPreferences(API_SHARED_FILE, Context.MODE_PRIVATE).getLong(
        LAST_TIME_VISIT_SHARED_KEY, 0
    ) == 0L

fun setLastVisitTime(context: Context, currentTimeMillis: Long) =
    context.getSharedPreferences(API_SHARED_FILE, Context.MODE_PRIVATE).edit {
        putLong(LAST_TIME_VISIT_SHARED_KEY, currentTimeMillis)
        apply()
    }

fun Context.toPlurals(resId: Int, plural: Long): String {
    val resources = createConfigurationContext(Configuration(this.resources.configuration).apply {
        setLocale(Locale("ru"))
    }).resources
    return resources.getQuantityString(resId, plural.toInt(), plural.toInt())
}

object TimeConverter {
    fun convertSeconds(seconds: Long, context: Context): String {

        val inMinute = 60 // 60 seconds in minute
        val inHour = 3_600 // 60 minutes in hour
        val inDay = 86_400 // 24 hours in day
        val inMonth = 2_592_000 // 30 days in month
        val inYear = 31_104_000 // 12 months in year

        return when {
            seconds / inYear > 0 -> context.toPlurals(R.plurals.years, seconds / inYear)
            seconds / inMonth > 0 -> context.toPlurals(R.plurals.months, seconds / inMonth)
            seconds / inDay > 0 -> context.toPlurals(R.plurals.days, seconds / inDay)
            seconds / inHour > 0 -> context.toPlurals(R.plurals.hours, seconds / inHour)
            seconds / inMinute > 0 -> context.toPlurals(R.plurals.minutes, seconds / inMinute)
            else -> "менее минуты"
        } + " назад"
    }
}