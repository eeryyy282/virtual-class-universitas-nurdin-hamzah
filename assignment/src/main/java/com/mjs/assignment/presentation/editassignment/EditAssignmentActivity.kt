package com.mjs.assignment.presentation.editassignment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mjs.assignment.R
import com.mjs.assignment.databinding.ActivityEditAssignmentBinding
import com.mjs.assignment.di.assignmentModule
import com.mjs.assignment.presentation.detailassignment.DetailAssignmentActivity
import com.mjs.assignment.utils.DateUtils
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Assignment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import java.util.Calendar

class EditAssignmentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditAssignmentBinding
    private val editAssignmentViewModel: EditAssignmentViewModel by viewModel()
    private var currentAssignment: Assignment? = null
    private var selectedStartDate: Calendar? = null
    private var selectedDeadlineDate: Calendar? = null
    private var selectedAttachmentUri: Uri? = null

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                it.data?.data?.let { uri ->
                    selectedAttachmentUri = uri
                    binding.tvCurrentAttachment.text = getFileName(uri)
                    binding.tvCurrentAttachment.visibility = View.VISIBLE
                    binding.tvNoAttachmentEdit.visibility = View.GONE
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loadKoinModules(assignmentModule)
        binding = ActivityEditAssignmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        currentAssignment =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(
                    DetailAssignmentActivity.EXTRA_ASSIGNMENT,
                    Assignment::class.java,
                )
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(DetailAssignmentActivity.EXTRA_ASSIGNMENT)
            }

        if (currentAssignment == null) {
            Toast.makeText(this, "Error: Data tugas tidak ditemukan", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        populateAssignmentDetails()
        setupClickListeners()
        observeViewModel()
        checkDarkMode()
        setupOnBackPressed()
    }

    private fun populateAssignmentDetails() {
        currentAssignment?.let { assignment ->
            binding.etAssignmentTitle.setText(assignment.assignmentTitle)
            binding.etAssignmentDescription.setText(assignment.description)

            selectedStartDate = Calendar.getInstance().apply { time = assignment.startDate }
            selectedDeadlineDate = Calendar.getInstance().apply { time = assignment.deadlineDate }

            selectedStartDate?.time?.let { date ->
                binding.etStartDate.setText(DateUtils.formatDeadline(date))
            }
            selectedDeadlineDate?.time?.let { date ->
                binding.etDeadlineDate.setText(DateUtils.formatDeadline(date))
            }

            if (!assignment.attachment.isNullOrBlank()) {
                binding.tvCurrentAttachment.text = assignment.attachment
                binding.tvCurrentAttachment.visibility = View.VISIBLE
                binding.tvNoAttachmentEdit.visibility = View.GONE
            } else {
                binding.tvCurrentAttachment.visibility = View.GONE
                binding.tvNoAttachmentEdit.visibility = View.VISIBLE
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnBackEditAssignment.setOnClickListener { showExitConfirmationDialog() }

        binding.etStartDate.setOnClickListener {
            showDateTimePicker(true)
        }

        binding.etDeadlineDate.setOnClickListener {
            showDateTimePicker(false)
        }

        binding.btnChangeAttachment.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "*/*"
                }
            filePickerLauncher.launch(intent)
        }

        binding.btnSaveAssignmentChanges.setOnClickListener {
            showSaveConfirmationDialog()
        }
    }

    private fun showDateTimePicker(isStartDate: Boolean) {
        val calendar =
            if (isStartDate) {
                selectedStartDate ?: Calendar.getInstance()
            } else {
                selectedDeadlineDate
                    ?: Calendar.getInstance()
            }
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                TimePickerDialog(
                    this,
                    { _, hourOfDay, minute ->
                        val selectedDateTime =
                            Calendar.getInstance().apply {
                                set(Calendar.YEAR, year)
                                set(Calendar.MONTH, month)
                                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                set(Calendar.HOUR_OF_DAY, hourOfDay)
                                set(Calendar.MINUTE, minute)
                            }
                        if (isStartDate) {
                            selectedStartDate = selectedDateTime
                            binding.etStartDate.setText(DateUtils.formatDeadline(selectedDateTime.time))
                        } else {
                            selectedDeadlineDate = selectedDateTime
                            binding.etDeadlineDate.setText(DateUtils.formatDeadline(selectedDateTime.time))
                        }
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true,
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
        ).show()
    }

    private fun saveChanges() {
        val title =
            binding.etAssignmentTitle.text
                .toString()
                .trim()
        val description =
            binding.etAssignmentDescription.text
                .toString()
                .trim()

        if (title.isEmpty()) {
            binding.tilAssignmentTitle.error = "Judul tidak boleh kosong"
            return
        } else {
            binding.tilAssignmentTitle.error = null
        }

        if (description.isEmpty()) {
            binding.tilAssignmentDescription.error = "Deskripsi tidak boleh kosong"
            return
        } else {
            binding.tilAssignmentDescription.error = null
        }

        if (selectedStartDate == null) {
            Toast.makeText(this, "Tanggal mulai harus dipilih", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedDeadlineDate == null) {
            Toast.makeText(this, "Tanggal selesai harus dipilih", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedStartDate!!.after(selectedDeadlineDate)) {
            Toast
                .makeText(
                    this,
                    "Tanggal mulai tidak boleh setelah tanggal selesai",
                    Toast.LENGTH_SHORT,
                ).show()
            return
        }

        val attachmentName =
            if (selectedAttachmentUri != null) getFileName(selectedAttachmentUri!!) else currentAssignment?.attachment

        currentAssignment?.let { assignment ->
            val updatedAssignment =
                assignment.copy(
                    assignmentTitle = title,
                    description = description,
                    startDate = selectedStartDate!!.time,
                    deadlineDate = selectedDeadlineDate!!.time,
                    attachment = attachmentName,
                )
            editAssignmentViewModel.updateAssignment(updatedAssignment)
        }
    }

    private fun observeViewModel() {
        editAssignmentViewModel.updateAssignmentResult.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    Toast.makeText(this, "Menyimpan perubahan...", Toast.LENGTH_SHORT).show()
                }

                is Resource.Success -> {
                    Toast.makeText(this, resource.data, Toast.LENGTH_LONG).show()
                    setResult(RESULT_OK)
                    finish()
                }

                is Resource.Error -> {
                    Toast
                        .makeText(
                            this,
                            resource.message ?: "Gagal menyimpan perubahan",
                            Toast.LENGTH_LONG,
                        ).show()
                }
            }
        }
    }

    private fun getFileName(uri: Uri): String? {
        var fileName: String? = null
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    fileName = cursor.getString(displayNameIndex)
                }
            }
        }
        return fileName
    }

    private fun checkDarkMode() {
        editAssignmentViewModel.getThemeSetting.observe(this) {
            if (it) {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
            } else {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            }
        }
    }

    private fun setupOnBackPressed() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showExitConfirmationDialog()
                }
            },
        )
    }

    private fun showExitConfirmationDialog() {
        val dialog =
            AlertDialog
                .Builder(this)
                .setTitle("Konfirmasi Keluar")
                .setMessage("Apakah Anda yakin ingin keluar? Semua perubahan yang belum disimpan akan hilang.")
                .setPositiveButton("Ya") { _, _ -> finish() }
                .setNegativeButton("Tidak", null)
                .show()
        dialog
            .getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(getColor(com.mjs.core.R.color.outline_color_theme))
        dialog
            .getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(getColor(com.mjs.core.R.color.outline_color_theme))
    }

    private fun showSaveConfirmationDialog() {
        val dialog =
            AlertDialog
                .Builder(this)
                .setTitle("Konfirmasi Simpan")
                .setMessage("Apakah Anda yakin ingin menyimpan perubahan ini?")
                .setPositiveButton("Ya") { _, _ -> saveChanges() }
                .setNegativeButton("Tidak", null)
                .show()
        dialog
            .getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(getColor(com.mjs.core.R.color.outline_color_theme))
        dialog
            .getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(getColor(com.mjs.core.R.color.outline_color_theme))
    }
}
