package com.mjs.lecturer.utils

import java.util.Date

object DateUtils {
    fun isDateAfterCurrent(date: Date?): Boolean {
        if (date == null) return false
        return date.after(Date())
    }
}
