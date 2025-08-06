package com.mjs.core.domain.repository

import com.mjs.core.data.Resource
import com.mjs.core.data.source.local.entity.AssignmentEntity
import com.mjs.core.data.source.local.entity.AttendanceEntity
import com.mjs.core.data.source.local.entity.EnrollmentEntity
import com.mjs.core.data.source.local.entity.MahasiswaEntity
import com.mjs.core.data.source.local.entity.PostEntity
import com.mjs.core.data.source.local.entity.SubmissionEntity
import com.mjs.core.domain.model.Dosen
import com.mjs.core.domain.model.Forum
import com.mjs.core.domain.model.Kehadiran
import com.mjs.core.domain.model.Kelas
import com.mjs.core.domain.model.Mahasiswa
import com.mjs.core.domain.model.Materi
import com.mjs.core.domain.model.Postingan
import com.mjs.core.domain.model.Tugas
import kotlinx.coroutines.flow.Flow

interface IRepository {
    fun getMahasiswaByNim(nim: String): Flow<Resource<Mahasiswa>>

    fun getDosenByNidn(nidn: String): Flow<Resource<Dosen>>

    suspend fun registerMahasiswa(mahasiswa: MahasiswaEntity): Flow<Resource<String>>

    fun getAllKelas(): Flow<Resource<List<Kelas>>>

    fun getEnrolledClasses(nim: String): Flow<Resource<List<EnrollmentEntity>>>

    suspend fun enrollToClass(enrollment: EnrollmentEntity): Flow<Resource<String>>

    fun getMaterialsByClass(kelasId: Int): Flow<Resource<List<Materi>>>

    fun getAssignmentsByClass(kelasId: Int): Flow<Resource<List<Tugas>>>

    suspend fun insertAssignment(assignment: AssignmentEntity): Flow<Resource<String>>

    suspend fun insertSubmission(submission: SubmissionEntity): Flow<Resource<String>>

    fun getForumsByClass(kelasId: Int): Flow<Resource<List<Forum>>>

    fun getPostsByForum(forumId: Int): Flow<Resource<List<Postingan>>>

    suspend fun insertPost(post: PostEntity): Flow<Resource<String>>

    fun getAttendanceHistory(
        nim: String,
        kelasId: Int,
    ): Flow<Resource<List<Kehadiran>>>

    suspend fun insertAttendance(attendance: AttendanceEntity): Flow<Resource<String>>
}
