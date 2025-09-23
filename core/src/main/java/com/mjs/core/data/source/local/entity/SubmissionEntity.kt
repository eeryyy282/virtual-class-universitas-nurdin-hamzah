package com.mjs.core.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

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
            entity = StudentEntity::class,
            parentColumns = ["student_id"],
            childColumns = ["student_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class SubmissionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "submission_id")
    val submissionId: Long = 0L,
    @ColumnInfo(name = "assignment_id")
    val assignmentId: Long,
    @ColumnInfo(name = "student_id")
    val studentId: Long,
    @ColumnInfo(name = "attachment")
    val attachment: String? = null,
    @ColumnInfo(name = "submission_date")
    val submissionDate: Date,
    val grade: Int? = null,
    val note: String? = null,
)
