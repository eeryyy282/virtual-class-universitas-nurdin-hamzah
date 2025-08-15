@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.mjs.detailclass.utils

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

object TimeFormat {
    fun formatSchedule(schedule: String): String {
        val parts = schedule.split(", ")
        if (parts.size < 2) return schedule

        val timePart = parts[1].split(" - ")
        if (timePart.size < 2) return schedule

        val startTimeStr = timePart[0]
        val endTimeStrWithWIB = timePart[1]
        val endTimeStr = endTimeStrWithWIB.replace(" WIB", "")

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        try {
            val startTime = sdf.parse(startTimeStr)
            val endTime = sdf.parse(endTimeStr)
            val durationMillis = endTime.time - startTime.time
            val hours = TimeUnit.MILLISECONDS.toHours(durationMillis)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis) % 60
            return "$startTimeStr - $endTimeStr WIB | $hours Jam $minutes Menit"
        } catch (_: Exception) {
            return schedule
        }
    }
}
