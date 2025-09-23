package com.mjs.course.presentation.registered

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.mjs.core.data.Resource
import com.mjs.core.data.source.local.pref.AppPreference
import com.mjs.core.domain.model.Course
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import com.mjs.course.R
import com.mjs.course.databinding.ActivityDetailCourseRegisteredBinding
import com.mjs.course.di.courseModule
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import java.text.SimpleDateFormat
import java.util.Locale

@Suppress("DEPRECATION")
class DetailCourseRegisteredActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailCourseRegisteredBinding
    private val detailCourseRegisteredViewModel: DetailCourseRegisteredViewModel by viewModel()
    private var courseId: String? = null
    private var currentStudentId: Long? = null
    private var currentUserType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        loadKoinModules(courseModule)
        binding = ActivityDetailCourseRegisteredBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        courseId = intent.getStringExtra(EXTRA_COURSE_ID)

        detailCourseRegisteredViewModel.loggedInUserId.observe(this) { userId ->
            currentStudentId = userId
        }

        detailCourseRegisteredViewModel.loggedInUserType.observe(this) { userType ->
            currentUserType = userType
            setupButtonVisibility()
        }

        checkDarkMode()
        observeCourseDetails()
        observeLecturerDetails()
        observeLeaveCourseStatus()
        observePendingEnrollmentCount()

        if (this.courseId != null) {
            detailCourseRegisteredViewModel.fetchCourseDetails(this.courseId!!)
        } else {
            Toast
                .makeText(
                    this,
                    getString(R.string.cannot_find_course_id_error),
                    Toast.LENGTH_LONG,
                ).show()
            binding.progressBarDetailCourse.visibility = View.GONE
            finish()
        }
    }

    private fun setupButtonVisibility() {
        if (currentUserType == VirtualClassUseCase.USER_TYPE_LECTURER) {
            binding.btnLeaveCourseOrRequest.setImageDrawable(
                AppCompatResources.getDrawable(this, R.drawable.invitation_icon),
            )
            binding.btnLeaveCourseOrRequest.setColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.outline_color_theme,
                ),
            )
            binding.tvCountInvitation.visibility = View.GONE
        } else {
            binding.btnLeaveCourseOrRequest.setImageDrawable(
                AppCompatResources.getDrawable(this, R.drawable.exit_icon),
            )
            binding.btnLeaveCourseOrRequest.setColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.red_mahogany,
                ),
            )
            binding.tvCountInvitation.visibility = View.GONE
        }
    }

    private fun observeCourseDetails() {
        detailCourseRegisteredViewModel.courseDetails.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarDetailCourse.visibility = View.VISIBLE
                }

                is Resource.Success -> {
                    resource.data?.let {
                        setupView(it)
                        setupActionListeners()
                    } ?: run {
                        binding.progressBarDetailCourse.visibility = View.GONE
                        Toast
                            .makeText(
                                this,
                                getString(R.string.cannot_find_course_detail),
                                Toast.LENGTH_LONG,
                            ).show()
                    }
                }

                is Resource.Error -> {
                    binding.progressBarDetailCourse.visibility = View.GONE
                    Toast
                        .makeText(
                            this,
                            resource.message ?: getString(R.string.failed_to_load_course_data),
                            Toast.LENGTH_LONG,
                        ).show()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeLecturerDetails() {
        detailCourseRegisteredViewModel.lecturerDetail.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarDetailCourse.visibility = View.VISIBLE
                    binding.tvLecturerName.text = getString(R.string.lecturer_name_loading)
                    binding.tvLecturerId.text = getString(R.string.lecturer_id_loading)
                }

                is Resource.Success -> {
                    binding.progressBarDetailCourse.visibility = View.GONE
                    resource.data?.let {
                        binding.tvLecturerName.text = it.name
                        binding.tvLecturerId.text =
                            "${getString(R.string.nidn_prefix)}${it.lecturerId}"
                        Glide
                            .with(this)
                            .load(it.profilePicture)
                            .placeholder(R.drawable.profile_photo)
                            .error(R.drawable.profile_photo)
                            .into(binding.ivPhotoProfileLecturer)
                    } ?: run {
                        binding.tvLecturerName.text =
                            getString(R.string.lecturer_name_not_available)
                        binding.tvLecturerId.visibility = View.GONE
                    }
                }

                is Resource.Error -> {
                    binding.progressBarDetailCourse.visibility = View.GONE
                    binding.tvLecturerName.text =
                        resource.message ?: getString(R.string.failed_to_load_lecturer_name)
                    binding.tvLecturerId.visibility = View.GONE
                    Toast
                        .makeText(
                            this,
                            resource.message ?: getString(R.string.failed_to_load_lecturer_name),
                            Toast.LENGTH_LONG,
                        ).show()
                }
            }
        }
    }

    private fun observePendingEnrollmentCount() {
        detailCourseRegisteredViewModel.pendingEnrollmentCount.observe(this) { resource ->
            if (currentUserType == VirtualClassUseCase.USER_TYPE_LECTURER) {
                when (resource) {
                    is Resource.Success -> {
                        val count = resource.data
                        if (count != null && count > 0) {
                            binding.tvCountInvitation.text = count.toString()
                            binding.tvCountInvitation.visibility = View.VISIBLE
                        } else {
                            binding.tvCountInvitation.visibility = View.GONE
                        }
                    }

                    is Resource.Error -> {
                        binding.tvCountInvitation.visibility = View.GONE
                    }

                    is Resource.Loading -> {
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupView(course: Course) {
        binding.tvSubjectName.text = course.courseName
        binding.tvCourseRoom.text = course.room
        binding.tvCreditsSubject.text = getString(R.string.sks, course.credit)
        binding.tvMajorSubject.text = course.department

        try {
            val dayFormat = SimpleDateFormat("EEEE", Locale("in", "ID"))
            binding.tvDaySchedule.text = dayFormat.format(course.schedule)
        } catch (_: Exception) {
            binding.tvDaySchedule.text = getString(R.string.day_not_available)
        }

        try {
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            binding.tvScheduleCourse.text = "${timeFormat.format(course.schedule)} WIB"
        } catch (_: Exception) {
            binding.tvScheduleCourse.text = getString(R.string.schedule_time_not_available)
        }

        if (!course.courseImage.isNullOrEmpty()) {
            Glide
                .with(this)
                .load(course.courseImage)
                .placeholder(R.drawable.profile_photo)
                .error(R.drawable.profile_photo)
                .into(binding.ivPhotoProfileCourse)
        } else {
            binding.ivPhotoProfileCourse.setImageResource(R.drawable.profile_photo)
        }
    }

    private fun setupActionListeners() {
        val currentCourseIdValue = this.courseId ?: return

        if (currentUserType == AppPreference.USER_TYPE_STUDENT) {
            binding.btnLeaveCourseOrRequest.setOnClickListener {
                showLeaveCourseConfirmationDialog()
            }
        } else {
            binding.btnLeaveCourseOrRequest.setOnClickListener {
                val uri =
                    "enroll_course://enroll_course_request_activity?courseId=$currentCourseIdValue".toUri()
                startActivity(Intent(Intent.ACTION_VIEW, uri))
            }
        }
    }

    private fun showLeaveCourseConfirmationDialog() {
        AlertDialog
            .Builder(this)
            .setTitle(getString(R.string.leave_course_confirmation_title))
            .setMessage(getString(R.string.leave_course_confirmation_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                if (currentStudentId != null && this.courseId != null) {
                    detailCourseRegisteredViewModel.leaveCourse(
                        currentStudentId!!,
                        this.courseId!!,
                    )
                } else {
                    Toast
                        .makeText(
                            this,
                            getString(R.string.cannot_leave_course_error),
                            Toast.LENGTH_SHORT,
                        ).show()
                }
            }.setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun observeLeaveCourseStatus() {
        detailCourseRegisteredViewModel.leaveCourseStatus.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarDetailCourse.visibility = View.VISIBLE
                }

                is Resource.Success -> {
                    binding.progressBarDetailCourse.visibility = View.GONE
                    Toast.makeText(this, resource.data, Toast.LENGTH_SHORT).show()
                    val resultIntent = Intent()
                    setResult(RESULT_REFRESH_REQUESTED, resultIntent)
                    finish()
                }

                is Resource.Error -> {
                    binding.progressBarDetailCourse.visibility = View.GONE
                    Toast
                        .makeText(
                            this,
                            resource.message ?: getString(R.string.failed_to_leave_course),
                            Toast.LENGTH_SHORT,
                        ).show()
                }
            }
        }
    }

    private fun checkDarkMode() {
        detailCourseRegisteredViewModel.getThemeSetting.observe(this) {
            if (it) {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
            } else {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            }
        }
    }

    companion object {
        const val EXTRA_COURSE_ID = "courseId"
        const val RESULT_REFRESH_REQUESTED = RESULT_FIRST_USER + 1
    }
}
