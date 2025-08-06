package com.mjs.core.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "submissions",
    foreignKeys = [
        ForeignKey(
            entity = AssignmentEntity::class,
            parentColumns = ["assignment_id"],
            childColumns = ["assignment_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = MahasiswaEntity::class,
            parentColumns = ["nim"],
            childColumns = ["nim"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class SubmissionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "submission_id")
    val submissionId: Int = 0,
    @ColumnInfo(name = "assignment_id")
    val assignmentId: Int,
    val nim: String,
    @ColumnInfo(name = "file_url")
    val fileUrl: String,
    @ColumnInfo(name = "submission_date")
    val submissionDate: String,
    val grade: String? = null,
    val feedback: String? = null,
)
