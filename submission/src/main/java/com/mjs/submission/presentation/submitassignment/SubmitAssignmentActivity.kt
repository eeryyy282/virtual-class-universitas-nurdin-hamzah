package com.mjs.submission.presentation.submitassignment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.mjs.core.data.Resource
import com.mjs.submission.R
import com.mjs.submission.databinding.ActivitySubmitAssignmentBinding
import com.mjs.submission.di.submissionModule
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules

class SubmitAssignmentActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySubmitAssignmentBinding
    private var assignmentId: Long = -1L
    private var selectedFileUri: Uri? = null

    private val submitAssignmentViewModel: SubmitAssignmentViewModel by viewModel()

    companion object {
        const val EXTRA_ASSIGNMENT_ID = "extra_assignment_id_for_submit"
    }

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let { uri ->
                    selectedFileUri = uri
                    binding.tvSelectedFileName.text =
                        uri.lastPathSegment
                            ?: getString(R.string.file_selected)
                    binding.tvSelectedFileName.visibility = View.VISIBLE
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubmitAssignmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadKoinModules(submissionModule)
        assignmentId = intent.getLongExtra(EXTRA_ASSIGNMENT_ID, -1L)

        if (assignmentId == -1L) {
            Toast
                .makeText(
                    this,
                    getString(R.string.error_assignment_id_not_founded),
                    Toast.LENGTH_LONG,
                ).show()
            finish()
            return
        }

        checkDarkMode()
        setupClickListeners()
        observeViewModel()
    }

    private fun checkDarkMode() {
        submitAssignmentViewModel.getThemeSetting.observe(this) {
            if (it) {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
            } else {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnSelectFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            filePickerLauncher.launch(intent)
        }

        binding.btnSubmitFinal.setOnClickListener {
            val notes =
                binding.etSubmissionNotes.text
                    .toString()
                    .trim()
            val filePath = selectedFileUri?.toString()

            submitAssignmentViewModel.submitTask(assignmentId, notes, filePath)
        }
    }

    private fun observeViewModel() {
        submitAssignmentViewModel.submissionStatus.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.btnSubmitFinal.isEnabled = false
                    binding.btnSubmitFinal.text =
                        getString(R.string.sending_)
                }

                is Resource.Success -> {
                    binding.btnSubmitFinal.isEnabled = true
                    binding.btnSubmitFinal.text =
                        getString(R.string.send_task)
                    Toast
                        .makeText(
                            this,
                            resource.data
                                ?: getString(R.string.task_succesfull_sending),
                            Toast.LENGTH_LONG,
                        ).show()
                    finish()
                }

                is Resource.Error -> {
                    binding.btnSubmitFinal.isEnabled = true
                    binding.btnSubmitFinal.text =
                        getString(R.string.send_task)
                    Toast
                        .makeText(
                            this,
                            resource.message
                                ?: getString(R.string.failed_send_task),
                            Toast.LENGTH_LONG,
                        ).show()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.main -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
}
