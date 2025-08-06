package com.mjs.core.domain.usecase.auth

import com.mjs.core.domain.repository.IRepository

class GetMahasiswaUseCase(
    private val repository: IRepository,
) {
    operator fun invoke(nim: String) = repository.getMahasiswaByNim(nim)
}
