package com.mjs.detailtask.presentation.submittask

//noinspection SuspiciousImport
import android.R
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
import com.mjs.detailtask.databinding.ActivitySubmitTaskBinding
import com.mjs.detailtask.di.detailTaskModule
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules

class SubmitTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySubmitTaskBinding
    private var assignmentId: Int = -1
    private var selectedFileUri: Uri? = null

    private val submitTaskViewModel: SubmitTaskViewModel by viewModel()

    companion object {
        const val EXTRA_ASSIGNMENT_ID = "extra_assignment_id"
    }

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let { uri ->
                    selectedFileUri = uri
                    binding.tvSelectedFileName.text =
                        uri.lastPathSegment ?: getString(com.mjs.detailtask.R.string.file_selected)
                    binding.tvSelectedFileName.visibility = View.VISIBLE
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubmitTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadKoinModules(detailTaskModule)
        assignmentId = intent.getIntExtra(EXTRA_ASSIGNMENT_ID, -1)

        if (assignmentId == -1) {
            Toast
                .makeText(
                    this,
                    getString(com.mjs.detailtask.R.string.error_assignment_id_not_founded),
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
        submitTaskViewModel.getThemeSetting.observe(this) {
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

        binding.btnSubmitTaskFinal.setOnClickListener {
            val notes =
                binding.etSubmissionNotes.text
                    .toString()
                    .trim()
            val filePath = selectedFileUri?.toString()

            submitTaskViewModel.submitTask(assignmentId, notes, filePath)
        }
    }

    private fun observeViewModel() {
        submitTaskViewModel.submissionStatus.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.btnSubmitTaskFinal.isEnabled = false
                    binding.btnSubmitTaskFinal.text =
                        getString(com.mjs.detailtask.R.string.sending_)
                }

                is Resource.Success -> {
                    binding.btnSubmitTaskFinal.isEnabled = true
                    binding.btnSubmitTaskFinal.text =
                        getString(com.mjs.detailtask.R.string.send_task)
                    Toast
                        .makeText(
                            this,
                            resource.data
                                ?: getString(com.mjs.detailtask.R.string.task_succesfull_sending),
                            Toast.LENGTH_LONG,
                        ).show()
                    finish()
                }

                is Resource.Error -> {
                    binding.btnSubmitTaskFinal.isEnabled = true
                    binding.btnSubmitTaskFinal.text =
                        getString(com.mjs.detailtask.R.string.send_task)
                    Toast
                        .makeText(
                            this,
                            resource.message
                                ?: getString(com.mjs.detailtask.R.string.failed_send_task),
                            Toast.LENGTH_LONG,
                        ).show()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
}
