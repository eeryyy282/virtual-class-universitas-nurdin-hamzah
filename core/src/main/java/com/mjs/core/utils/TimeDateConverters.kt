@file:Suppress("ktlint:standard:filename")

package com.mjs.core.utils

import androidx.room.TypeConverter
import java.util.Date

object TimeDateConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time
}
