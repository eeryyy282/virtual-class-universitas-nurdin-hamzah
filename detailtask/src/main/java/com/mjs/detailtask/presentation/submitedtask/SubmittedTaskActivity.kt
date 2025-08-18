package com.mjs.detailtask.presentation.submitedtask

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
import com.mjs.core.ui.task.SubmittedTaskAdapter
import com.mjs.detailtask.R
import com.mjs.detailtask.databinding.ActivitySubmittedTaskBinding
import com.mjs.detailtask.di.detailTaskModule
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules

class SubmittedTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySubmittedTaskBinding
    private val submittedTaskViewModel: SubmittedTaskViewModel by viewModel()
    private lateinit var submittedTaskAdapter: SubmittedTaskAdapter
    private var assignmentId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loadKoinModules(detailTaskModule)
        binding = ActivitySubmittedTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        assignmentId = intent.getIntExtra(EXTRA_ASSIGNMENT_ID, -1)

        setupRecyclerView()
        checkDarkMode()
        observeSubmittedTasks()

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        if (assignmentId != -1) {
            submittedTaskViewModel.getSubmissionsByAssignment(assignmentId)
        } else {
            binding.pbSubmittedTasks.visibility = View.GONE
            binding.tvNoSubmissions.visibility = View.VISIBLE
            binding.tvNoSubmissions.text = getString(R.string.error_loading_submissions)
        }
    }

    private fun setupRecyclerView() {
        submittedTaskAdapter = SubmittedTaskAdapter()
        binding.rvSubmittedTasks.apply {
            layoutManager = LinearLayoutManager(this@SubmittedTaskActivity)
            adapter = submittedTaskAdapter
        }
    }

    private fun observeSubmittedTasks() {
        submittedTaskViewModel.submittedTasks.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.pbSubmittedTasks.visibility = View.VISIBLE
                    binding.tvNoSubmissions.visibility = View.GONE
                    binding.rvSubmittedTasks.visibility = View.GONE
                }

                is Resource.Success -> {
                    binding.pbSubmittedTasks.visibility = View.GONE
                    val submissions = resource.data
                    if (submissions.isNullOrEmpty()) {
                        binding.tvNoSubmissions.visibility = View.VISIBLE
                        binding.rvSubmittedTasks.visibility = View.GONE
                    } else {
                        binding.tvNoSubmissions.visibility = View.GONE
                        binding.rvSubmittedTasks.visibility = View.VISIBLE
                        submittedTaskAdapter.submitList(submissions)
                    }
                }

                is Resource.Error -> {
                    binding.pbSubmittedTasks.visibility = View.GONE
                    binding.tvNoSubmissions.visibility = View.VISIBLE
                    binding.rvSubmittedTasks.visibility = View.GONE
                    binding.tvNoSubmissions.text =
                        resource.message ?: getString(R.string.error_loading_submissions)
                    Toast
                        .makeText(
                            this,
                            resource.message ?: getString(R.string.error_loading_submissions),
                            Toast.LENGTH_LONG,
                        ).show()
                }
            }
        }
    }

    private fun checkDarkMode() {
        submittedTaskViewModel.getThemeSetting.observe(this) {
            if (it) {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
            } else {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            }
        }
    }

    companion object {
        const val EXTRA_ASSIGNMENT_ID = "extra_assignment_id"
    }
}
