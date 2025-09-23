package com.mjs.core.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "materials",
    foreignKeys = [
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["course_id"],
            childColumns = ["course_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class MaterialEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "material_id")
    val materialId: Long = 0L,
    @ColumnInfo(name = "course_id")
    val courseId: String,
    @ColumnInfo(name = "material_title")
    val materialTitle: String,
    val description: String,
    val attachment: String? = null,
    @ColumnInfo(name = "upload_date")
    val uploadDate: Date,
    @ColumnInfo(name = "type")
    val type: String? = null,
)
