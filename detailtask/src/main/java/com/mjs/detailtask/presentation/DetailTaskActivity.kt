package com.mjs.detailtask.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Tugas
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import com.mjs.detailtask.R
import com.mjs.detailtask.databinding.ActivityDetailTaskBinding
import com.mjs.detailtask.di.detailTaskModule
import com.mjs.detailtask.presentation.edittask.EditTaskActivity
import com.mjs.detailtask.presentation.submitedtask.SubmittedTaskActivity
import com.mjs.detailtask.presentation.submittask.SubmitTaskActivity
import com.mjs.detailtask.utils.DateUtils
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules

class DetailTaskActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetailTaskBinding
    private val detailTaskViewModel: DetailTaskViewModel by viewModel()
    private var currentTaskForEdit: Tugas? = null
    private var currentTaskId: Int = -1

    companion object {
        const val EXTRA_TASK = "extra_task"
        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_TASK_ID_FOR_SUBMIT = "extra_task_id_for_submit"
    }

    private val editTaskLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                if (currentTaskId != -1) {
                    detailTaskViewModel.loadTaskAndClassDetailsById(currentTaskId)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loadKoinModules(detailTaskModule)
        binding = ActivityDetailTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        currentTaskId = intent.getIntExtra(EXTRA_TASK_ID, -1)

        if (currentTaskId != -1) {
            detailTaskViewModel.loadTaskAndClassDetailsById(currentTaskId)
            observeTaskDetails()
            observeUserRole()
            observeErrorState()
        } else {
            Toast.makeText(this, "Error: ID Tugas tidak ditemukan", Toast.LENGTH_LONG).show()
            finish()
        }

        setupClickListeners()
        checkDarkMode()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnEditTask.setOnClickListener {
            currentTaskForEdit?.let { taskToEdit ->
                val intent =
                    Intent(this, EditTaskActivity::class.java).apply {
                        putExtra(EXTRA_TASK, taskToEdit)
                    }
                editTaskLauncher.launch(intent)
            } ?: Toast
                .makeText(this, "Data tugas untuk diedit tidak ditemukan", Toast.LENGTH_SHORT)
                .show()
        }

        binding.btnActionTask.setOnClickListener {
            val role = detailTaskViewModel.userRole.value
            detailTaskViewModel.tugasDetail.value?.let { tugas ->
                if (role == VirtualClassUseCase.USER_TYPE_DOSEN) {
                    val intent =
                        Intent(this, SubmittedTaskActivity::class.java).apply {
                            putExtra(SubmittedTaskActivity.EXTRA_ASSIGNMENT_ID, tugas.assignmentId)
                        }
                    startActivity(intent)
                } else if (role == VirtualClassUseCase.USER_TYPE_MAHASISWA) {
                    val intent =
                        Intent(this, SubmitTaskActivity::class.java).apply {
                            putExtra(EXTRA_TASK_ID_FOR_SUBMIT, tugas.assignmentId)
                        }
                    startActivity(intent)
                }
            } ?: Toast
                .makeText(
                    this,
                    "Error: Data tugas tidak ditemukan untuk aksi ini",
                    Toast.LENGTH_SHORT,
                ).show()
        }
    }

    private fun observeUserRole() {
        detailTaskViewModel.userRole.observe(this) { role ->
            when (role) {
                VirtualClassUseCase.USER_TYPE_DOSEN -> {
                    binding.btnEditTask.visibility = View.VISIBLE
                    binding.btnActionTask.text = getString(R.string.view_submissions)
                    binding.btnActionTask.visibility = View.VISIBLE
                }

                VirtualClassUseCase.USER_TYPE_MAHASISWA -> {
                    binding.btnEditTask.visibility = View.GONE
                    binding.btnActionTask.text =
                        getString(R.string.submit_task_button)
                    binding.btnActionTask.visibility = View.VISIBLE
                }

                else -> {
                    binding.btnEditTask.visibility = View.GONE
                    binding.btnActionTask.visibility = View.GONE
                }
            }
        }
    }

    private fun observeTaskDetails() {
        detailTaskViewModel.tugasDetail.observe(this) { tugas ->
            if (tugas != null) {
                currentTaskForEdit = tugas
                binding.tvTaskTitle.text = tugas.judulTugas
                binding.tvTaskDescription.text = tugas.deskripsi
                binding.tvStartDate.text = DateUtils.formatDeadline(tugas.tanggalMulai)
                binding.tvDeadlineDate.text = DateUtils.formatDeadline(tugas.tanggalSelesai)

                val taskAttachment = tugas.attachment
                if (taskAttachment != null && taskAttachment.isNotBlank()) {
                    binding.ivAttachmentIcon.visibility = View.VISIBLE
                    binding.tvAttachmentName.visibility = View.VISIBLE
                    binding.tvAttachmentName.text = taskAttachment
                    binding.tvNoAttachment.visibility = View.GONE
                    binding.tvAttachmentName.setOnClickListener {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, taskAttachment.toUri())
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

        detailTaskViewModel.kelasDetail.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.pbClassDetails.visibility = View.VISIBLE
                    binding.tvClassName.visibility = View.GONE
                    binding.ivClassPhoto.visibility = View.GONE
                }

                is Resource.Success -> {
                    binding.pbClassDetails.visibility = View.GONE
                    binding.tvClassName.visibility = View.VISIBLE
                    binding.ivClassPhoto.visibility = View.VISIBLE
                    val kelas = resource.data
                    if (kelas != null) {
                        binding.tvClassName.text = kelas.namaKelas
                        Glide
                            .with(this)
                            .load(kelas.classImage)
                            .placeholder(R.drawable.subject_photo_profile)
                            .error(R.drawable.subject_photo_profile)
                            .into(binding.ivClassPhoto)
                    } else {
                        binding.tvClassName.text = getString(R.string.unknown_class_name)
                        Glide
                            .with(this)
                            .load(R.drawable.subject_photo_profile)
                            .into(binding.ivClassPhoto)
                    }
                }

                is Resource.Error -> {
                    binding.pbClassDetails.visibility = View.GONE
                    binding.tvClassName.text = getString(R.string.unknown_class_name)
                    binding.ivClassPhoto.visibility = View.VISIBLE
                    Glide
                        .with(this)
                        .load(R.drawable.subject_photo_profile)
                        .into(binding.ivClassPhoto)
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
        detailTaskViewModel.errorState.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                detailTaskViewModel.clearErrorState()
                if (detailTaskViewModel.tugasDetail.value == null) {
                    finish()
                }
            }
        }
    }

    private fun checkDarkMode() {
        detailTaskViewModel.getThemeSetting.observe(this) {
            if (it) {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
            } else {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            }
        }
    }
}
