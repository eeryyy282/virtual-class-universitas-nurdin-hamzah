package com.mjs.core.utils

import com.mjs.core.data.source.local.entity.AssignmentEntity
import com.mjs.core.data.source.local.entity.AttendanceEntity
import com.mjs.core.data.source.local.entity.DosenEntity
import com.mjs.core.data.source.local.entity.ForumEntity
import com.mjs.core.data.source.local.entity.KelasEntity
import com.mjs.core.data.source.local.entity.MahasiswaEntity
import com.mjs.core.data.source.local.entity.MaterialEntity
import com.mjs.core.data.source.local.entity.PostEntity
import com.mjs.core.domain.model.Dosen
import com.mjs.core.domain.model.Forum
import com.mjs.core.domain.model.Kehadiran
import com.mjs.core.domain.model.Kelas
import com.mjs.core.domain.model.Mahasiswa
import com.mjs.core.domain.model.Materi
import com.mjs.core.domain.model.Postingan
import com.mjs.core.domain.model.Tugas

object DataMapper {
    fun mapMahasiswaEntityToDomain(input: MahasiswaEntity): Mahasiswa =
        Mahasiswa(
            nim = input.nim,
            nama = input.nama,
            email = input.email,
            fotoProfil = input.fotoProfil,
            dosenPembimbing = input.dosenPembimbing,
            jurusan = input.jurusan,
        )

    fun mapDosenEntityToDomain(input: DosenEntity): Dosen =
        Dosen(
            nidn = input.nidn,
            nama = input.nama,
            email = input.email,
            fotoProfil = input.fotoProfil,
        )

    fun mapKelasEntityToDomain(input: KelasEntity): Kelas =
        Kelas(
            kelasId = input.kelasId,
            namaKelas = input.namaKelas,
            deskripsi = input.deskripsi,
            nidn = input.nidn,
            jadwal = input.jadwal,
            semester = input.semester,
            credit = input.credit,
            jurusan = input.jurusan,
            classImage = input.classImage,
            ruang = input.ruang,
        )

    fun mapKelasEntitiesToDomains(input: List<KelasEntity>): List<Kelas> = input.map { mapKelasEntityToDomain(it) }

    fun mapTugasEntityToDomain(input: AssignmentEntity): Tugas =
        Tugas(
            assignmentId = input.assignmentId,
            kelasId = input.kelasId,
            judulTugas = input.judulTugas,
            deskripsi = input.deskripsi,
            tanggalMulai = input.tanggalMulai,
            tanggalSelesai = input.tanggalSelesai,
            attachment = input.attachment,
        )

    fun mapTugasEntitiesToDomains(input: List<AssignmentEntity>): List<Tugas> = input.map { mapTugasEntityToDomain(it) }

    fun mapMateriEntityToDomain(input: MaterialEntity): Materi =
        Materi(
            materiId = input.materiId,
            kelasId = input.kelasId,
            judulMateri = input.judulMateri,
            deskripsi = input.deskripsi,
            attachment = input.attachment,
            tanggalUpload = input.tanggalUpload,
            tipe = input.tipe,
        )

    fun mapMateriEntitiesToDomains(input: List<MaterialEntity>): List<Materi> = input.map { mapMateriEntityToDomain(it) }

    fun mapForumEntityToDomain(input: ForumEntity): Forum =
        Forum(
            forumId = input.forumId,
            kelasId = input.kelasId,
            judulForum = input.judulForum,
            deskripsi = input.deskripsi,
        )

    fun mapForumEntitiesToDomains(input: List<ForumEntity>): List<Forum> = input.map { mapForumEntityToDomain(it) }

    fun mapPostinganEntityToDomain(input: PostEntity): Postingan =
        Postingan(
            postId = input.postId,
            forumId = input.forumId,
            userId = input.userId,
            userRole = input.userRole,
            isiPost = input.isiPost,
            tanggalPost = input.tanggalPost,
        )

    fun mapPostinganEntitiesToDomains(input: List<PostEntity>): List<Postingan> = input.map { mapPostinganEntityToDomain(it) }

    fun mapKehadiranEntityToDomain(input: AttendanceEntity): Kehadiran =
        Kehadiran(
            absensiId = input.absensiId,
            kelasId = input.kelasId,
            nim = input.nim,
            tanggalHadir = input.tanggalHadir,
            status = input.status,
            keterangan = input.keterangan,
        )

    fun mapKehadiranEntitiesToDomains(input: List<AttendanceEntity>): List<Kehadiran> = input.map { mapKehadiranEntityToDomain(it) }
}
