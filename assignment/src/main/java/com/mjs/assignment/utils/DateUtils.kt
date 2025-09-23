@file:Suppress("DEPRECATION")

package com.mjs.assignment.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

object DateUtils {
    fun formatDeadline(date: Date): String =
        try {
            val outputFormat =
                SimpleDateFormat("HH.mm 'WIB' | dd MMMM yyyy", Locale("id", "ID"))
            outputFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
            val formattedDate = outputFormat.format(date)

            val currentTime = Calendar.getInstance().time
            val diffInMillis = date.time - currentTime.time

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
        } catch (e: Exception) {
            e.printStackTrace()
            date.toString()
        }
}
