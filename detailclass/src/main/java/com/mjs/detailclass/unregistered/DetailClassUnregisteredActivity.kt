package com.mjs.detailclass.unregistered

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Kelas
import com.mjs.detailclass.R
import com.mjs.detailclass.databinding.ActivityDetailClassUnregisteredBinding
import com.mjs.detailclass.di.detailClassModule
import com.mjs.detailclass.utils.TimeFormat
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules

class DetailClassUnregisteredActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailClassUnregisteredBinding
    private val detailClassViewModel: DetailClassUnregisteredViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        loadKoinModules(detailClassModule)
        binding = ActivityDetailClassUnregisteredBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        checkDarkMode()

        val kelasId = intent.getStringExtra("kelasId")
        if (kelasId != null) {
            detailClassViewModel.fetchKelasDetails(kelasId)
        } else {
            Toast.makeText(this, "Error: Kelas ID tidak ditemukan", Toast.LENGTH_LONG).show()
            finish()
        }

        observeKelasDetails()
        observeDosenDetails()
    }

    private fun observeKelasDetails() {
        detailClassViewModel.kelasDetails.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarDetailClass.visibility = View.VISIBLE
                    binding.ivErrorDetailClass.visibility = View.GONE
                    binding.tvErrorDetailClass.visibility = View.GONE
                }

                is Resource.Success -> {
                    binding.progressBarDetailClass.visibility = View.GONE
                    binding.ivErrorDetailClass.visibility = View.GONE
                    binding.tvErrorDetailClass.visibility = View.GONE
                    resource.data?.let { bindKelasData(it) }
                }

                is Resource.Error -> {
                    binding.progressBarDetailClass.visibility = View.GONE
                    binding.ivErrorDetailClass.visibility = View.VISIBLE
                    binding.tvErrorDetailClass.visibility = View.VISIBLE
                    binding.tvErrorDetailClass.text =
                        resource.message ?: "Gagal memuat detail kelas"
                    Toast
                        .makeText(
                            this,
                            resource.message ?: "Gagal memuat detail kelas",
                            Toast.LENGTH_LONG,
                        ).show()
                }
            }
        }
    }

    private fun observeDosenDetails() {
        detailClassViewModel.dosenDetails.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                }

                is Resource.Success -> {
                    resource.data?.let {
                        binding.tvLectureClassroom.text = it.nama
                        binding.tvLectureId.text = it.nidn.toString()
                        Glide
                            .with(this)
                            .load(it.fotoProfil)
                            .placeholder(R.drawable.profile_photo)
                            .error(R.drawable.profile_photo)
                            .into(binding.photoProfileLecture)
                    }
                }

                is Resource.Error -> {
                    binding.tvLectureClassroom.text = getString(R.string.lecture_user_not_found)
                    binding.tvLectureId.text = ""
                    Glide
                        .with(this)
                        .load(R.drawable.profile_photo)
                        .into(binding.photoProfileLecture)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindKelasData(kelas: Kelas) {
        binding.tvSubjectName.text = kelas.namaKelas
        binding.tvClassroomLocation.text = kelas.ruang
        binding.tvCreditsSubject.text = "${kelas.credit} SKS"
        binding.tvCategorySubject.text = kelas.jurusan

        val scheduleParts = kelas.jadwal.split(", ")
        if (scheduleParts.isNotEmpty()) {
            binding.tvDaySchedule.text = scheduleParts[0].trim()
        } else {
            binding.tvDaySchedule.text = getString(R.string.day_not_available)
        }
        binding.tvScheduleClassroom.text = TimeFormat.formatSchedule(kelas.jadwal)

        Glide
            .with(this)
            .load(kelas.classImage)
            .placeholder(R.drawable.profile_photo)
            .error(R.drawable.profile_photo)
            .into(binding.ivPhotoProfileClassroom)
    }

    private fun checkDarkMode() {
        detailClassViewModel.getThemeSetting.observe(this) {
            if (it) {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
            } else {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            }
        }
    }
}
