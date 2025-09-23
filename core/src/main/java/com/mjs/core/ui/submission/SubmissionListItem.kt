package com.mjs.core.ui.submission

import com.mjs.core.data.source.local.entity.SubmissionEntity

data class SubmissionListItem(
    val submissionEntity: SubmissionEntity,
    val studentName: String?,
    val studentPhotoUrl: String?,
)
