package com.mjs.enrollcourse.presentation.enrollrequest

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Student
import com.mjs.core.ui.StudentEnrollRequestAdapter
import com.mjs.enrollcourse.databinding.ActivityEnrollRequestBinding
import com.mjs.enrollcourse.di.enrollCourseModule
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules

class EnrollRequestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEnrollRequestBinding
    private val enrollRequestViewModel: EnrollRequestViewModel by viewModel()
    private var courseId: String? = null
    private lateinit var studentEnrollRequestAdapter: StudentEnrollRequestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        loadKoinModules(enrollCourseModule)
        binding = ActivityEnrollRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        createNotificationChannel()
        setupRecyclerView()

        courseId = intent.data?.getQueryParameter("courseId")

        if (courseId != null) {
            observeEnrollmentRequests()
            observeEnrollmentUpdateStatus()
            enrollRequestViewModel.fetchEnrollmentRequests(courseId!!)
        } else {
            Toast.makeText(this, "Error: Course ID tidak ditemukan", Toast.LENGTH_LONG).show()
            binding.progressBarEnrollRequest.visibility = View.GONE
            finish()
        }

        checkDarkMode()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Permintaan Pendaftaran"
            val descriptionText = "Notifikasi untuk permintaan pendaftaran mahasiswa baru"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(
                    EnrollRequestViewModel.ENROLL_REQUEST_CHANNEL_ID,
                    name,
                    importance,
                ).apply {
                    description = descriptionText
                }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setupRecyclerView() {
        studentEnrollRequestAdapter =
            StudentEnrollRequestAdapter(
                onAcceptClickListener = { student ->
                    handleAcceptRequest(student)
                },
                onRejectClickListener = { student ->
                    handleRejectRequest(student)
                },
            )
        binding.rvListStudentRequestEnroll.apply {
            layoutManager = LinearLayoutManager(this@EnrollRequestActivity)
            adapter = studentEnrollRequestAdapter
        }
    }

    private fun observeEnrollmentRequests() {
        enrollRequestViewModel.enrollmentRequests.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarEnrollRequest.visibility = View.VISIBLE
                    binding.rvListStudentRequestEnroll.visibility = View.GONE
                    binding.ivNoStudentRequest.visibility = View.GONE
                    binding.tvNoCourseAvailable.visibility = View.GONE
                }

                is Resource.Success -> {
                    binding.progressBarEnrollRequest.visibility = View.GONE
                    val studentList = resource.data
                    if (studentList != null && studentList.isNotEmpty()) {
                        studentEnrollRequestAdapter.submitList(studentList)
                        binding.rvListStudentRequestEnroll.visibility = View.VISIBLE
                        binding.ivNoStudentRequest.visibility = View.GONE
                        binding.tvNoCourseAvailable.visibility = View.GONE
                    } else {
                        studentEnrollRequestAdapter.submitList(emptyList())
                        binding.rvListStudentRequestEnroll.visibility = View.GONE
                        binding.ivNoStudentRequest.visibility = View.VISIBLE
                        binding.tvNoCourseAvailable.visibility = View.VISIBLE
                    }
                }

                is Resource.Error -> {
                    binding.progressBarEnrollRequest.visibility = View.GONE
                    binding.rvListStudentRequestEnroll.visibility = View.GONE
                    binding.ivNoStudentRequest.visibility = View.VISIBLE
                    binding.tvNoCourseAvailable.visibility = View.VISIBLE
                    Toast
                        .makeText(
                            this,
                            resource.message ?: "Gagal memuat daftar permintaan",
                            Toast.LENGTH_LONG,
                        ).show()
                }
            }
        }
    }

    private fun observeEnrollmentUpdateStatus() {
        enrollRequestViewModel.enrollmentUpdateStatus.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                }

                is Resource.Success -> {
                    Toast.makeText(this, resource.data, Toast.LENGTH_SHORT).show()
                    if (courseId != null) {
                        enrollRequestViewModel.fetchEnrollmentRequests(courseId!!)
                    }
                }

                is Resource.Error -> {
                    Toast
                        .makeText(
                            this,
                            resource.message ?: "Gagal memperbarui status",
                            Toast.LENGTH_SHORT,
                        ).show()
                }
            }
        }
    }

    private fun handleAcceptRequest(student: Student) {
        if (courseId != null) {
            enrollRequestViewModel.acceptEnrollmentRequest(student.studentId, courseId!!)
        } else {
            Toast.makeText(this, "Error: Course ID tidak valid", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleRejectRequest(student: Student) {
        if (courseId != null) {
            enrollRequestViewModel.rejectEnrollmentRequest(student.studentId, courseId!!)
        } else {
            Toast.makeText(this, "Error: Course ID tidak valid", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkDarkMode() {
        enrollRequestViewModel.getThemeSetting.observe(this) {
            if (it) {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
            } else {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            }
        }
    }
}
