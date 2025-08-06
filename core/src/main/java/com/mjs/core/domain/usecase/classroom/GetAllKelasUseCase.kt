package com.mjs.core.domain.usecase.classroom

import com.mjs.core.domain.repository.IRepository

class GetAllKelasUseCase(
    private val repository: IRepository,
) {
    operator fun invoke() = repository.getAllKelas()
}
