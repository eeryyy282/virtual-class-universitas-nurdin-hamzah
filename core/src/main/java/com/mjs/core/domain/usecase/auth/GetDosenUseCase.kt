package com.mjs.core.domain.usecase.auth

import com.mjs.core.domain.repository.IRepository

class GetDosenUseCase(
    private val repository: IRepository,
) {
    operator fun invoke(nidn: String) = repository.getDosenByNidn(nidn)
}
