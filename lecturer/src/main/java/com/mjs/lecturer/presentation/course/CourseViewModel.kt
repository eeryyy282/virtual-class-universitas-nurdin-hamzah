package com.mjs.lecturer.presentation.course

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Course
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class CourseViewModel(
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    val lecturerCoursesData: StateFlow<Resource<Pair<List<Pair<String, List<Course>>>, Int>>> =
        virtualClassUseCase
            .getLoggedInUserId()
            .flatMapLatest { lecturerId ->
                if (lecturerId == null) {
                    flowOf(Resource.Error("ID Dosen pengguna tidak ditemukan. Silakan masuk kembali."))
                } else {
                    virtualClassUseCase.getAllSchedulesByLecturerId(lecturerId).map { resource ->
                        when (resource) {
                            is Resource.Loading -> Resource.Loading()
                            is Resource.Error ->
                                Resource.Error(
                                    resource.message ?: "Gagal mengambil data mata kuliah",
                                )

                            is Resource.Success -> {
                                val allCourses = resource.data ?: emptyList()
                                val totalCourses = allCourses.size
                                val grouped =
                                    allCourses
                                        .groupBy { it.semester }
                                        .map { Pair(it.key, it.value) }
                                Resource.Success(Pair(grouped, totalCourses))
                            }
                        }
                    }
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Resource.Loading(),
            )

    val groupedCourses: StateFlow<Resource<List<Pair<String, List<Course>>>>> =
        lecturerCoursesData
            .map { resource ->
                when (resource) {
                    is Resource.Loading -> Resource.Loading()
                    is Resource.Error ->
                        Resource.Error(
                            resource.message ?: "Gagal mengambil data mata kuliah",
                        )

                    is Resource.Success -> {
                        val data = resource.data
                        if (data != null) {
                            Resource.Success(data.first)
                        } else {
                            Resource.Success(emptyList())
                        }
                    }
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Resource.Loading(),
            )

    val totalCourses: StateFlow<Resource<Int>> =
        lecturerCoursesData
            .map { resource ->
                when (resource) {
                    is Resource.Loading -> Resource.Loading()
                    is Resource.Error ->
                        Resource.Error(
                            resource.message ?: "Gagal mengambil data mata kuliah",
                        )

                    is Resource.Success -> {
                        val data = resource.data
                        if (data != null) {
                            Resource.Success(data.second)
                        } else {
                            Resource.Success(0)
                        }
                    }
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Resource.Loading(),
            )
}
