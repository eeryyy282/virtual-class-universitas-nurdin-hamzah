package com.mjs.core.domain.usecase.forum

import com.mjs.core.domain.repository.IRepository

class GetPostsUseCase(
    private val repository: IRepository,
) {
    operator fun invoke(forumId: Int) = repository.getPostsByForum(forumId)
}
