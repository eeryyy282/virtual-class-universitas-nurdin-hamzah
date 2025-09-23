package com.mjs.course.presentation.unregistered

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Course
import com.mjs.core.domain.model.Student
import com.mjs.core.ui.StudentListAdapter
import com.mjs.course.R
import com.mjs.course.databinding.ActivityDetailCourseUnregisteredBinding
import com.mjs.course.di.courseModule
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import java.text.SimpleDateFormat
import java.util.Locale

class DetailCourseUnregisteredActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailCourseUnregisteredBinding
    private val detailCourseViewModel: DetailCourseUnregisteredViewModel by viewModel()
    private lateinit var studentListAdapter: StudentListAdapter
    private var currentCourseId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        loadKoinModules(courseModule)
        binding = ActivityDetailCourseUnregisteredBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        checkDarkMode()
        setupRecyclerView()

        currentCourseId = intent.getStringExtra("courseId")
        if (currentCourseId != null) {
            detailCourseViewModel.fetchCourseDetailsAndEnrollmentStatus(currentCourseId!!)
        } else {
            Toast.makeText(this, "Error: Course ID tidak ditemukan", Toast.LENGTH_LONG).show()
            finish()
        }

        observeCourseDetails()
        observeLecturerDetails()
        observeStudentList()
        observeStudentCount()
        observeEnrollmentRequestStatus()
        observeCurrentEnrollmentState()

        binding.btnEnroll.setOnClickListener {
            showEnrollConfirmationDialog()
        }
    }

    private fun showEnrollConfirmationDialog() {
        currentCourseId?.let { courseIdValue ->
            AlertDialog
                .Builder(this)
                .setTitle("Konfirmasi Pendaftaran")
                .setMessage("Apakah Anda yakin ingin mendaftar di course ini?")
                .setPositiveButton("Ya") { _, _ ->
                    detailCourseViewModel.enrollToCourse(courseIdValue)
                }.setNegativeButton("Tidak", null)
                .show()
        }
    }

    private fun observeCurrentEnrollmentState() {
        detailCourseViewModel.currentEnrollmentState.observe(this) { enrollment ->
            if (enrollment != null) {
                when (enrollment.status) {
                    "pending" -> {
                        binding.btnEnroll.isEnabled = false
                        binding.btnEnroll.text = getString(R.string.enrollment_pending)
                    }

                    "approved" -> {
                        binding.btnEnroll.isEnabled = false
                        binding.btnEnroll.text = getString(R.string.already_enrolled)
                    }

                    else -> {
                        binding.btnEnroll.isEnabled = true
                        binding.btnEnroll.text = getString(R.string.enroll)
                    }
                }
            } else {
                binding.btnEnroll.isEnabled = true
                binding.btnEnroll.text = getString(R.string.enroll)
            }
        }
    }

    private fun observeEnrollmentRequestStatus() {
        detailCourseViewModel.enrollmentRequestStatus.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarDetailCourse.visibility = View.VISIBLE
                }

                is Resource.Success -> {
                    binding.progressBarDetailCourse.visibility = View.GONE
                    Toast.makeText(this, resource.data, Toast.LENGTH_LONG).show()
                }

                is Resource.Error -> {
                    binding.progressBarDetailCourse.visibility = View.GONE
                    Toast
                        .makeText(this, resource.message ?: "Gagal mendaftar", Toast.LENGTH_LONG)
                        .show()
                    val currentState = detailCourseViewModel.currentEnrollmentState.value
                    if (currentState == null || currentState.status != "approved") {
                        binding.btnEnroll.isEnabled = true
                        binding.btnEnroll.text = getString(R.string.enroll)
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        studentListAdapter =
            StudentListAdapter { student: Student ->
            }
        binding.rvMemberStudents.apply {
            layoutManager = LinearLayoutManager(this@DetailCourseUnregisteredActivity)
            adapter = studentListAdapter
        }
    }

    private fun observeStudentList() {
        detailCourseViewModel.studentList.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                }

                is Resource.Success -> {
                    resource.data?.let {
                        studentListAdapter.submitList(it)
                    }
                }

                is Resource.Error -> {
                    Toast
                        .makeText(
                            this,
                            resource.message ?: "Gagal memuat daftar mahasiswa",
                            Toast.LENGTH_SHORT,
                        ).show()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeStudentCount() {
        detailCourseViewModel.studentCount.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                }

                is Resource.Success -> {
                    resource.data?.let {
                        if (it != 0) {
                            binding.tvMemberStudentCountTotal.text =
                                "$it Mahasiswa bergabung di mata kuliah ini."
                        } else {
                            binding.tvMemberStudentCountTotal.text =
                                "Belum ada mahasiswa yang bergabung di mata kuliah ini."
                        }
                    }
                }

                is Resource.Error -> {
                    binding.tvMemberStudentCountTotal.text =
                        "Terjadi kesalahan saat memuat jumlah mahasiswa."
                    Toast
                        .makeText(
                            this,
                            resource.message ?: "Gagal memuat jumlah mahasiswa",
                            Toast.LENGTH_SHORT,
                        ).show()
                }
            }
        }
    }

    private fun observeCourseDetails() {
        detailCourseViewModel.courseDetails.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarDetailCourse.visibility = View.VISIBLE
                    binding.ivErrorDetailCourse.visibility = View.GONE
                    binding.tvErrorDetailCourse.visibility = View.GONE
                }

                is Resource.Success -> {
                    binding.progressBarDetailCourse.visibility = View.GONE
                    binding.ivErrorDetailCourse.visibility = View.GONE
                    binding.tvErrorDetailCourse.visibility = View.GONE
                    resource.data?.let { bindCourseData(it) }
                }

                is Resource.Error -> {
                    binding.progressBarDetailCourse.visibility = View.GONE
                    binding.ivErrorDetailCourse.visibility = View.VISIBLE
                    binding.tvErrorDetailCourse.visibility = View.VISIBLE
                    binding.tvErrorDetailCourse.text =
                        resource.message ?: "Gagal memuat detail mata kuliah"
                    Toast
                        .makeText(
                            this,
                            resource.message ?: "Gagal memuat detail mata kuliah",
                            Toast.LENGTH_LONG,
                        ).show()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeLecturerDetails() {
        detailCourseViewModel.lecturerDetails.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                }

                is Resource.Success -> {
                    resource.data?.let {
                        binding.tvLecturerName.text = it.name
                        binding.tvLecturerId.text = "NIDN: ${it.lecturerId}"
                        Glide
                            .with(this)
                            .load(it.profilePicture)
                            .placeholder(R.drawable.profile_photo)
                            .error(R.drawable.profile_photo)
                            .into(binding.ivPhotoProfileLecturer)
                    }
                }

                is Resource.Error -> {
                    binding.tvLecturerName.text = getString(R.string.lecturer_user_not_found)
                    binding.tvLecturerId.text = ""
                    Glide
                        .with(this)
                        .load(R.drawable.profile_photo)
                        .into(binding.ivPhotoProfileLecturer)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindCourseData(course: Course) {
        binding.tvSubjectName.text = course.courseName
        binding.tvCourseRoom.text = course.room
        binding.tvCreditsSubject.text = "${course.credit} SKS"
        binding.tvMajorSubject.text = course.department

        try {
            val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
            binding.tvDaySchedule.text = dayFormat.format(course.schedule)

            val fullScheduleFormat = SimpleDateFormat("EEEE, HH:mm", Locale.getDefault())
            binding.tvScheduleCourse.text = fullScheduleFormat.format(course.schedule)
        } catch (_: Exception) {
            binding.tvDaySchedule.text = getString(R.string.day_not_available)
            binding.tvScheduleCourse.text = "Gagal menampilkan jadwal"
        }

        Glide
            .with(this)
            .load(course.courseImage)
            .placeholder(R.drawable.profile_photo)
            .error(R.drawable.profile_photo)
            .into(binding.ivPhotoProfileCourse)
    }

    private fun checkDarkMode() {
        detailCourseViewModel.getThemeSetting.observe(this) {
            if (it) {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
            } else {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            }
        }
    }
}
