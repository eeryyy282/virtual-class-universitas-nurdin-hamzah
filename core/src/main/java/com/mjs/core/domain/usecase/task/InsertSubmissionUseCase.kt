package com.mjs.core.domain.usecase.task

import com.mjs.core.data.source.local.entity.SubmissionEntity
import com.mjs.core.domain.repository.IRepository

class InsertSubmissionUseCase(
    private val repository: IRepository,
) {
    suspend operator fun invoke(submission: SubmissionEntity) = repository.insertSubmission(submission)
}
