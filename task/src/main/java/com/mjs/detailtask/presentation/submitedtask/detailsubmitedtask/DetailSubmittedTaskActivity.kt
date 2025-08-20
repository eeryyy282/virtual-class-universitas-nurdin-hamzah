package com.mjs.detailtask.presentation.submitedtask.detailsubmitedtask

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.mjs.core.data.Resource
import com.mjs.core.ui.task.SubmissionListItem
import com.mjs.detailtask.R
import com.mjs.detailtask.databinding.ActivityDetailSubmittedTaskBinding
import com.mjs.detailtask.di.taskModule
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules

class DetailSubmittedTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailSubmittedTaskBinding
    private val detailSubmittedTaskViewModel: DetailSubmittedTaskViewModel by viewModel()
    private var submissionId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loadKoinModules(taskModule)
        binding = ActivityDetailSubmittedTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        submissionId = intent.getIntExtra(EXTRA_SUBMISSION_ID, -1)

        checkDarkMode()
        setupTextWatchers()
        observeSubmissionDetail()
        observeUpdateResult()
        setupBackButtonInterceptor()

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSaveGrade.setOnClickListener {
            showConfirmSaveDialog()
        }

        if (submissionId != -1) {
            detailSubmittedTaskViewModel.getSubmissionDetail(submissionId)
        } else {
            showError(getString(R.string.error_submission_id_not_found))
            finish()
        }
    }

    private fun setupTextWatchers() {
        val textWatcher =
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int,
                ) {
                }

                override fun afterTextChanged(s: Editable?) {
                    detailSubmittedTaskViewModel.checkIfUnsavedChangesExist(
                        binding.etGrade.text.toString(),
                        binding.etNote.text.toString(),
                    )
                }
            }
        binding.etGrade.addTextChangedListener(textWatcher)
        binding.etNote.addTextChangedListener(textWatcher)
    }

    private fun observeSubmissionDetail() {
        detailSubmittedTaskViewModel.submissionDetail.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.contentView.visibility = View.GONE
                }

                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.contentView.visibility = View.VISIBLE
                    resource.data?.let {
                        populateUi(it)
                        detailSubmittedTaskViewModel.setInitialSubmissionData(
                            it.submissionEntity.grade,
                            it.submissionEntity.note,
                        )
                    } ?: showError(getString(R.string.error_submission_data_not_found))
                }

                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.contentView.visibility = View.GONE
                    showError(resource.message ?: getString(R.string.failed_load_submission_detail))
                }
            }
        }
    }

    private fun populateUi(submissionListItem: SubmissionListItem) {
        val submission = submissionListItem.submissionEntity
        binding.tvStudentName.text =
            submissionListItem.studentName ?: getString(R.string.not_available)
        binding.tvStudentNim.text = getString(R.string.nim_format, submission.nim.toString())
        Glide
            .with(this)
            .load(submissionListItem.studentPhotoUrl)
            .placeholder(R.drawable.profile_photo)
            .error(R.drawable.profile_photo)
            .into(binding.ivStudentPhoto)

        binding.tvSubmissionDate.text = submission.submissionDate
        if (submission.attachment != null) {
            binding.tvAttachmentName.text = submission.attachment
            binding.ivAttachmentIcon.visibility = View.VISIBLE
            binding.tvAttachmentName.visibility = View.VISIBLE
            binding.tvNoAttachment.visibility = View.GONE
        } else {
            binding.ivAttachmentIcon.visibility = View.GONE
            binding.tvAttachmentName.visibility = View.GONE
            binding.tvNoAttachment.visibility = View.VISIBLE
        }

        binding.etGrade.setText(submission.grade?.toString() ?: "")
        binding.etNote.setText(submission.note ?: "")
    }

    private fun saveGradeAndNote() {
        val gradeString =
            binding.etGrade.text
                .toString()
                .trim()
        val note =
            binding.etNote.text
                .toString()
                .trim()

        val gradeInt =
            if (gradeString.isNotEmpty()) {
                try {
                    gradeString.toInt()
                } catch (_: NumberFormatException) {
                    Toast
                        .makeText(
                            this,
                            getString(R.string.invalid_grade_format),
                            Toast.LENGTH_SHORT,
                        ).show()
                    return
                }
            } else {
                null
            }

        if (gradeInt != null && (gradeInt < 0 || gradeInt > 100)) {
            Toast.makeText(this, getString(R.string.invalid_grade_range), Toast.LENGTH_SHORT).show()
            return
        }

        if (submissionId != -1) {
            detailSubmittedTaskViewModel.updateSubmissionGradeAndNote(
                submissionId,
                gradeInt,
                note.ifBlank { null },
            )
        }
    }

    private fun showConfirmSaveDialog() {
        AlertDialog
            .Builder(this)
            .setTitle(getString(R.string.confirm_save_title))
            .setMessage(getString(R.string.confirm_save_message))
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                saveGradeAndNote()
            }.setNegativeButton(getString(R.string.cancel_dialog)) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun observeUpdateResult() {
        detailSubmittedTaskViewModel.updateResult.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.btnSaveGrade.isEnabled = false
                }

                is Resource.Success -> {
                    binding.btnSaveGrade.isEnabled = true
                    Toast.makeText(this, resource.data, Toast.LENGTH_SHORT).show()
                }

                is Resource.Error -> {
                    binding.btnSaveGrade.isEnabled = true
                    Toast
                        .makeText(
                            this,
                            resource.message ?: getString(R.string.failed_update_grade),
                            Toast.LENGTH_LONG,
                        ).show()
                }
            }
        }
    }

    private fun setupBackButtonInterceptor() {
        val callback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (detailSubmittedTaskViewModel.hasUnsavedChanges.value == true) {
                        showUnsavedChangesDialog()
                    } else {
                        finish()
                    }
                }
            }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun showUnsavedChangesDialog() {
        lateinit var observerInstance: Observer<Resource<String>>
        val dialog =
            AlertDialog
                .Builder(this)
                .setTitle(getString(R.string.unsaved_changes_title))
                .setMessage(getString(R.string.unsaved_changes_message))
                .setPositiveButton(getString(R.string.save)) { _, _ ->
                    saveGradeAndNote()
                    observerInstance =
                        Observer { resource ->
                            if (resource is Resource.Success) {
                                finish()
                                detailSubmittedTaskViewModel.updateResult.removeObserver(
                                    observerInstance,
                                )
                            } else if (resource is Resource.Error) {
                                detailSubmittedTaskViewModel.updateResult.removeObserver(
                                    observerInstance,
                                )
                            }
                        }
                    detailSubmittedTaskViewModel.updateResult.observe(this, observerInstance)
                }.setNegativeButton(getString(R.string.discard)) { _, _ ->
                    detailSubmittedTaskViewModel.discardChanges()
                    finish()
                }.setNeutralButton(getString(R.string.cancel_dialog)) { dialog, _ ->
                    dialog.dismiss()
                }.show()

        dialog
            .getButton(AlertDialog.BUTTON_POSITIVE)
            ?.setTextColor(ContextCompat.getColor(this, com.mjs.core.R.color.outline_color_theme))
        dialog
            .getButton(AlertDialog.BUTTON_NEGATIVE)
            ?.setTextColor(ContextCompat.getColor(this, com.mjs.core.R.color.outline_color_theme))
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun checkDarkMode() {
        detailSubmittedTaskViewModel.getThemeSetting.observe(this) { isDarkMode ->
            if (isDarkMode) {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
            } else {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            }
        }
    }

    companion object {
        const val EXTRA_SUBMISSION_ID = "extra_submission_id"
    }
}
