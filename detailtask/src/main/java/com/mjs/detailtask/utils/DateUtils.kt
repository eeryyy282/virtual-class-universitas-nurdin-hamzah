@file:Suppress("DEPRECATION")

package com.mjs.detailtask.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

object DateUtils {
    fun formatDeadline(dateString: String): String =
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            date?.let { deadlineDate ->
                val outputFormat =
                    SimpleDateFormat("HH.mm 'WIB' | dd MMMM yyyy", Locale("id", "ID"))
                outputFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
                val formattedDate = outputFormat.format(deadlineDate)

                val currentTime = Calendar.getInstance().time
                val diffInMillis = deadlineDate.time - currentTime.time

                val remainingTime =
                    when {
                        else -> {
                            val days = TimeUnit.MILLISECONDS.toDays(diffInMillis)
                            val hours = TimeUnit.MILLISECONDS.toHours(diffInMillis) % 24
                            val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis) % 60

                            when {
                                days > 0 -> "($days Hari Tersisa)"
                                hours > 0 -> "($hours Jam Tersisa)"
                                minutes > 0 -> "($minutes Menit Tersisa)"
                                else -> ""
                            }
                        }
                    }
                "$formattedDate $remainingTime"
            } ?: dateString
        } catch (e: Exception) {
            e.printStackTrace()
            dateString
        }

    fun parseDateString(dateString: String): Calendar? =
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            if (date != null) {
                Calendar.getInstance().apply { time = date }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
}
