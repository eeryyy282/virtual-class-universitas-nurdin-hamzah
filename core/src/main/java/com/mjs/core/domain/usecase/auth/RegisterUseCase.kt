package com.mjs.core.domain.usecase.auth

import com.mjs.core.data.source.local.entity.MahasiswaEntity
import com.mjs.core.domain.repository.IRepository

class RegisterUseCase(
    private val repository: IRepository,
) {
    suspend operator fun invoke(mahasiswa: MahasiswaEntity) = repository.registerMahasiswa(mahasiswa)
}
