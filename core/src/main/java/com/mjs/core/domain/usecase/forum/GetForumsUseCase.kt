package com.mjs.core.domain.usecase.forum

import com.mjs.core.domain.repository.IRepository

class GetForumsUseCase(
    private val repository: IRepository,
) {
    operator fun invoke(kelasId: Int) = repository.getForumsByClass(kelasId)
}
