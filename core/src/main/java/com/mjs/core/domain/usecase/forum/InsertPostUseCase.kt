package com.mjs.core.domain.usecase.forum

import com.mjs.core.data.source.local.entity.PostEntity
import com.mjs.core.domain.repository.IRepository

class InsertPostUseCase(
    private val repository: IRepository,
) {
    suspend operator fun invoke(post: PostEntity) = repository.insertPost(post)
}
