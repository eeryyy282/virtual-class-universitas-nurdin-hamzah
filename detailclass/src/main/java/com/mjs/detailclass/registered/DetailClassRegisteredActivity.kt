package com.mjs.detailclass.registered

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.mjs.core.data.Resource
import com.mjs.core.data.source.local.pref.AppPreference
import com.mjs.core.domain.model.Kelas
import com.mjs.detailclass.R
import com.mjs.detailclass.databinding.ActivityDetailClassRegisteredBinding
import com.mjs.detailclass.di.detailClassModule
import com.mjs.detailclass.utils.TimeFormat
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules

class DetailClassRegisteredActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailClassRegisteredBinding
    private val detailClassRegisteredViewModel: DetailClassRegisteredViewModel by viewModel()
    private var kelasId: String? = null
    private var currentNim: Int? = null
    private var currentUserType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        loadKoinModules(detailClassModule)
        binding = ActivityDetailClassRegisteredBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        kelasId = intent.getStringExtra(EXTRA_KELAS_ID)

        detailClassRegisteredViewModel.loggedInUserId.observe(this) { userId ->
            currentNim = userId
        }

        detailClassRegisteredViewModel.loggedInUserType.observe(this) { userType ->
            currentUserType = userType
            setupButtonVisibility()
            setupActionListeners()
        }

        checkDarkMode()
        observeClassDetails()
        observeDosenDetails()
        observeLeaveClassStatus()

        if (kelasId != null) {
            detailClassRegisteredViewModel.fetchClassDetails(kelasId!!)
        } else {
            Toast
                .makeText(this, getString(R.string.cannot_find_class_id_error), Toast.LENGTH_LONG)
                .show()
            binding.progressBarDetailClass.visibility = View.GONE
            finish()
        }
    }

    private fun setupButtonVisibility() {
        if (currentUserType != AppPreference.USER_TYPE_MAHASISWA) {
            binding.btnLeaveClassroomOrInvitation.setImageDrawable(
                AppCompatResources.getDrawable(this, R.drawable.invitation_icon),
            )
            binding.btnLeaveClassroomOrInvitation.setColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.outline_color_theme,
                ),
            )
        } else {
            binding.btnLeaveClassroomOrInvitation.setImageDrawable(
                AppCompatResources.getDrawable(this, R.drawable.exit_icon),
            )
            binding.btnLeaveClassroomOrInvitation.setColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.red_mahogany,
                ),
            )
        }
    }

    private fun observeClassDetails() {
        detailClassRegisteredViewModel.kelasDetails.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarDetailClass.visibility = View.VISIBLE
                }

                is Resource.Success -> {
                    resource.data?.let { setupView(it) }
                        ?: run {
                            binding.progressBarDetailClass.visibility = View.GONE
                            Toast
                                .makeText(
                                    this,
                                    getString(R.string.cannot_find_class_detail),
                                    Toast.LENGTH_LONG,
                                ).show()
                        }
                }

                is Resource.Error -> {
                    binding.progressBarDetailClass.visibility = View.GONE
                    Toast
                        .makeText(
                            this,
                            resource.message ?: getString(R.string.failed_to_load_class_data),
                            Toast.LENGTH_LONG,
                        ).show()
                }
            }
        }
    }

    private fun observeDosenDetails() {
        detailClassRegisteredViewModel.dosenDetail.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarDetailClass.visibility = View.VISIBLE
                    binding.tvLectureClassroom.text = getString(R.string.lecture_name_loading)
                    binding.tvLectureId.text = getString(R.string.lecture_id_loading)
                }

                is Resource.Success -> {
                    binding.progressBarDetailClass.visibility = View.GONE
                    resource.data?.let {
                        binding.tvLectureClassroom.text = it.nama
                        binding.tvLectureId.text = it.nidn.toString()
                        Glide
                            .with(this)
                            .load(it.fotoProfil)
                            .placeholder(R.drawable.profile_photo)
                            .error(R.drawable.profile_photo)
                            .into(binding.ivPhotoProfileLecture)
                    } ?: run {
                        binding.tvLectureClassroom.text =
                            getString(R.string.lecture_name_not_available)
                        binding.tvLectureId.visibility = View.GONE
                    }
                }

                is Resource.Error -> {
                    binding.progressBarDetailClass.visibility = View.GONE
                    binding.tvLectureClassroom.text =
                        resource.message ?: getString(R.string.failed_to_load_lecture_name)
                    binding.tvLectureId.visibility = View.GONE
                    Toast
                        .makeText(
                            this,
                            resource.message ?: getString(R.string.failed_to_load_lecture_name),
                            Toast.LENGTH_LONG,
                        ).show()
                }
            }
        }
    }

    private fun setupView(kelas: Kelas) {
        binding.tvSubjectName.text = kelas.namaKelas
        binding.tvClassroomLocation.text = kelas.ruang
        binding.tvCreditsSubject.text = getString(R.string.sks, kelas.credit)
        binding.tvMajorSubject.text = kelas.jurusan
        binding.tvScheduleClassroom.text = TimeFormat.formatSchedule(kelas.jadwal)
        binding.tvDaySchedule.text = kelas.jadwal.split(",")[0].trim()

        if (!kelas.classImage.isNullOrEmpty()) {
            Glide
                .with(this)
                .load(kelas.classImage)
                .placeholder(R.drawable.profile_photo)
                .error(R.drawable.profile_photo)
                .into(binding.ivPhotoProfileClassroom)
        } else {
            binding.ivPhotoProfileClassroom.setImageResource(R.drawable.profile_photo)
        }
    }

    private fun setupActionListeners() {
        if (currentUserType == AppPreference.USER_TYPE_MAHASISWA) {
            binding.btnLeaveClassroomOrInvitation.setOnClickListener {
                showLeaveClassConfirmationDialog()
            }
        } else {
            binding.btnLeaveClassroomOrInvitation.setOnClickListener {
                Toast
                    .makeText(
                        this,
                        "Fitur undangan untuk dosen akan segera hadir",
                        Toast.LENGTH_SHORT,
                    ).show()
            }
        }
    }

    private fun showLeaveClassConfirmationDialog() {
        AlertDialog
            .Builder(this)
            .setTitle(getString(R.string.leave_class_confirmation_title))
            .setMessage(getString(R.string.leave_class_confirmation_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                if (currentNim != null && kelasId != null) {
                    detailClassRegisteredViewModel.leaveClass(currentNim!!, kelasId!!)
                } else {
                    Toast
                        .makeText(
                            this,
                            getString(R.string.cannot_leave_class_error),
                            Toast.LENGTH_SHORT,
                        ).show()
                }
            }.setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun observeLeaveClassStatus() {
        detailClassRegisteredViewModel.leaveClassStatus.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarDetailClass.visibility = View.VISIBLE
                }

                is Resource.Success -> {
                    binding.progressBarDetailClass.visibility = View.GONE
                    Toast.makeText(this, resource.data, Toast.LENGTH_SHORT).show()
                    finish()
                }

                is Resource.Error -> {
                    binding.progressBarDetailClass.visibility = View.GONE
                    Toast
                        .makeText(
                            this,
                            resource.message ?: getString(R.string.failed_to_leave_class),
                            Toast.LENGTH_SHORT,
                        ).show()
                }
            }
        }
    }

    private fun checkDarkMode() {
        detailClassRegisteredViewModel.getThemeSetting.observe(this) {
            if (it) {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
            } else {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            }
        }
    }

    companion object {
        const val EXTRA_KELAS_ID = "kelasId"
    }
}
