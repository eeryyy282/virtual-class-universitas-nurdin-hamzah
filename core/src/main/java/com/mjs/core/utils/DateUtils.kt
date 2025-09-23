@file:Suppress("DEPRECATION")

package com.mjs.core.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Locale

object DateUtils {
    fun formatDeadline(dateString: String?): String {
        if (dateString.isNullOrEmpty()) {
            return ""
        }
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputTimeFormat = SimpleDateFormat("HH.mm", Locale.getDefault())
            val outputDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))

            val date = inputFormat.parse(dateString)
            if (date != null) {
                val timeString = outputTimeFormat.format(date)
                val dateStringOutput = outputDateFormat.format(date)
                "$timeString WIB | $dateStringOutput"
            } else {
                Log.w("DateUtils", "Gagal mem-parsing tanggal (hasil parse null): $dateString")
                dateString
            }
        } catch (e: Exception) {
            Log.e("DateUtils", "Error mem-parsing tanggal: $dateString", e)
            dateString
        }
    }

    fun formatScheduleTime(dateString: String?): String {
        if (dateString.isNullOrEmpty()) {
            return ""
        }
        return try {
            val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault())
            val outputTimeFormat = SimpleDateFormat("HH.mm", Locale("id", "ID"))

            val date = inputFormat.parse(dateString)
            if (date != null) {
                val timeString = outputTimeFormat.format(date)
                "$timeString WIB"
            } else {
                Log.w("DateUtils", "Gagal mem-parsing waktu (hasil parse null): $dateString")
                val scheduleParts = dateString.split(",")
                if (scheduleParts.isNotEmpty()) scheduleParts[0].trim() else dateString
            }
        } catch (e: Exception) {
            Log.e("DateUtils", "Error mem-parsing waktu: $dateString", e)
            val scheduleParts = dateString.split(",")
            if (scheduleParts.isNotEmpty()) scheduleParts[0].trim() else dateString
        }
    }
}
