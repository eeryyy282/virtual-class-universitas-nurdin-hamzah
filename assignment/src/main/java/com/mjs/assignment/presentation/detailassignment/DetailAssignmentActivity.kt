package com.mjs.assignment.presentation.detailassignment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.mjs.assignment.R
import com.mjs.assignment.databinding.ActivityDetailAssignmentBinding
import com.mjs.assignment.di.assignmentModule
import com.mjs.assignment.presentation.editassignment.EditAssignmentActivity
import com.mjs.assignment.utils.DateUtils
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Assignment
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import com.mjs.submission.presentation.listsubmission.ListSubmissionActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules

class DetailAssignmentActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetailAssignmentBinding
    private val detailAssignmentViewModel: DetailAssignmentViewModel by viewModel()
    private var currentAssignmentForEdit: Assignment? = null
    private var currentAssignmentId: Long = -1L

    companion object {
        const val EXTRA_ASSIGNMENT = "extra_assignment"
        const val EXTRA_ASSIGNMENT_ID = "extra_assignment_id"
        const val EXTRA_ASSIGNMENT_ID_FOR_SUBMIT = "extra_assignment_id_for_submit"
    }

    private val editAssignmentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                if (currentAssignmentId != -1L) {
                    detailAssignmentViewModel.loadAssignmentAndCourseDetailsById(currentAssignmentId)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loadKoinModules(assignmentModule)
        binding = ActivityDetailAssignmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        currentAssignmentId = intent.getLongExtra(EXTRA_ASSIGNMENT_ID, -1L)

        if (currentAssignmentId != -1L) {
            detailAssignmentViewModel.loadAssignmentAndCourseDetailsById(currentAssignmentId)
            observeAssignmentDetails()
            observeUserRole()
            observeErrorState()
            observeDeleteAssignmentResult()
        } else {
            Toast.makeText(this, "Error: ID Assignment tidak ditemukan", Toast.LENGTH_LONG).show()
            finish()
        }

        setupClickListeners()
        checkDarkMode()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnEditAssignment.setOnClickListener {
            currentAssignmentForEdit?.let { assignmentToEdit ->
                val intent =
                    Intent(this, EditAssignmentActivity::class.java).apply {
                        putExtra(EXTRA_ASSIGNMENT, assignmentToEdit)
                    }
                editAssignmentLauncher.launch(intent)
            } ?: Toast
                .makeText(this, "Data tugas untuk diedit tidak ditemukan", Toast.LENGTH_SHORT)
                .show()
        }

        binding.btnActionAssignment.setOnClickListener {
            val role = detailAssignmentViewModel.userRole.value
            detailAssignmentViewModel.assignmentDetail.value?.let { assignmentData ->
                if (role == VirtualClassUseCase.USER_TYPE_LECTURER) {
                    val uri = "submission://list_submission_activity".toUri()
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    intent.putExtra(
                        ListSubmissionActivity.EXTRA_ASSIGNMENT_ID,
                        assignmentData.assignmentId,
                    ) // Changed from courseId
                    startActivity(intent)
                } else if (role == VirtualClassUseCase.USER_TYPE_STUDENT) {
                    val uri = "submission://submit_assignment_activity".toUri()
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    intent.putExtra(
                        EXTRA_ASSIGNMENT_ID_FOR_SUBMIT,
                        assignmentData.assignmentId,
                    ) // Changed from courseId
                    startActivity(intent)
                }
            } ?: Toast
                .makeText(
                    this,
                    "Error: Data tugas tidak ditemukan untuk aksi ini",
                    Toast.LENGTH_SHORT,
                ).show()
        }

        binding.btnDelete.setOnClickListener {
            val dialog =
                AlertDialog
                    .Builder(this)
                    .setTitle("Hapus Assignment")
                    .setMessage("Apakah Anda yakin ingin menghapus tugas ini?")
                    .setPositiveButton(getString(R.string.delete_button_text)) { _, _ ->
                        if (currentAssignmentId != -1L) {
                            detailAssignmentViewModel.deleteAssignment(currentAssignmentId)
                        } else {
                            Toast
                                .makeText(
                                    this,
                                    "Error: ID Assignment tidak ditemukan",
                                    Toast.LENGTH_SHORT,
                                ).show()
                        }
                    }.setNegativeButton(getString(R.string.cancel_button_text), null)
                    .show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                ContextCompat.getColor(
                    this,
                    com.mjs.core.R.color.outline_color_theme,
                ),
            )
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                ContextCompat.getColor(
                    this,
                    com.mjs.core.R.color.outline_color_theme,
                ),
            )
        }
    }

    private fun observeUserRole() {
        detailAssignmentViewModel.userRole.observe(this) { role ->
            when (role) {
                VirtualClassUseCase.USER_TYPE_LECTURER -> {
                    binding.btnEditAssignment.visibility = View.VISIBLE
                    binding.btnDelete.visibility = View.VISIBLE
                    binding.btnActionAssignment.text = getString(R.string.view_submissions)
                    binding.btnActionAssignment.visibility = View.VISIBLE
                }

                VirtualClassUseCase.USER_TYPE_STUDENT -> {
                    binding.btnEditAssignment.visibility = View.GONE
                    binding.btnDelete.visibility = View.GONE
                    binding.btnActionAssignment.text =
                        getString(R.string.submit_assignment_button)
                    binding.btnActionAssignment.visibility = View.VISIBLE
                }

                else -> {
                    binding.btnEditAssignment.visibility = View.GONE
                    binding.btnDelete.visibility = View.GONE
                    binding.btnActionAssignment.visibility = View.GONE
                }
            }
        }
    }

    private fun observeAssignmentDetails() {
        detailAssignmentViewModel.assignmentDetail.observe(this) { assignment ->
            if (assignment != null) {
                currentAssignmentForEdit = assignment
                binding.tvAssignmentTitle.text = assignment.assignmentTitle
                binding.tvAssignmentDescription.text = assignment.description
                binding.tvStartDate.text = DateUtils.formatDeadline(assignment.startDate)
                binding.tvDeadlineDate.text = DateUtils.formatDeadline(assignment.deadlineDate)

                val assignmentAttachment = assignment.attachment
                if (assignmentAttachment != null && assignmentAttachment.isNotBlank()) {
                    binding.ivAttachmentIcon.visibility = View.VISIBLE
                    binding.tvAttachmentName.visibility = View.VISIBLE
                    binding.tvAttachmentName.text = assignmentAttachment
                    binding.tvNoAttachment.visibility = View.GONE
                    binding.tvAttachmentName.setOnClickListener {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, assignmentAttachment.toUri())
                            startActivity(intent)
                        } catch (_: Exception) {
                            Toast
                                .makeText(this, "Tidak dapat membuka lampiran", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                } else {
                    binding.ivAttachmentIcon.visibility = View.GONE
                    binding.tvAttachmentName.visibility = View.GONE
                    binding.tvNoAttachment.visibility = View.VISIBLE
                }
            }
        }

        detailAssignmentViewModel.courseDetail.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.pbCourseDetails.visibility = View.VISIBLE
                    binding.tvCourseName.visibility = View.GONE
                    binding.ivCoursePhoto.visibility = View.GONE
                }

                is Resource.Success -> {
                    binding.pbCourseDetails.visibility = View.GONE
                    binding.tvCourseName.visibility = View.VISIBLE
                    binding.ivCoursePhoto.visibility = View.VISIBLE
                    val course = resource.data
                    if (course != null) {
                        binding.tvCourseName.text = course.courseName
                        Glide
                            .with(this)
                            .load(course.courseImage)
                            .placeholder(com.mjs.core.R.drawable.course_photo_profile)
                            .error(com.mjs.core.R.drawable.course_photo_profile)
                            .into(binding.ivCoursePhoto)
                    } else {
                        binding.tvCourseName.text = getString(R.string.unknown_course_name)
                        Glide
                            .with(this)
                            .load(com.mjs.core.R.drawable.course_photo_profile)
                            .into(binding.ivCoursePhoto)
                    }
                }

                is Resource.Error -> {
                    binding.pbCourseDetails.visibility = View.GONE
                    binding.tvCourseName.text = getString(R.string.unknown_course_name)
                    binding.ivCoursePhoto.visibility = View.VISIBLE
                    Glide
                        .with(this)
                        .load(com.mjs.core.R.drawable.course_photo_profile)
                        .into(binding.ivCoursePhoto)
                    Toast
                        .makeText(
                            this,
                            resource.message ?: "Gagal memuat data kelas",
                            Toast.LENGTH_SHORT,
                        ).show()
                }
            }
        }
    }

    private fun observeErrorState() {
        detailAssignmentViewModel.errorState.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                detailAssignmentViewModel.clearErrorState()
                if (detailAssignmentViewModel.assignmentDetail.value == null) {
                    finish()
                }
            }
        }
    }

    private fun observeDeleteAssignmentResult() {
        detailAssignmentViewModel.deleteAssignmentResult.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.pbCourseDetails.visibility = View.VISIBLE
                }

                is Resource.Success -> {
                    binding.pbCourseDetails.visibility = View.GONE
                    Toast.makeText(this, resource.data, Toast.LENGTH_SHORT).show()
                    finish()
                }

                is Resource.Error -> {
                    binding.pbCourseDetails.visibility = View.GONE
                    Toast
                        .makeText(
                            this,
                            resource.message ?: "Gagal menghapus tugas",
                            Toast.LENGTH_SHORT,
                        ).show()
                }
            }
        }
    }

    private fun checkDarkMode() {
        detailAssignmentViewModel.getThemeSetting.observe(this) {
            if (it) {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
            } else {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            }
        }
    }
}
