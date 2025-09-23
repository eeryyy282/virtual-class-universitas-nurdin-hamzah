package com.mjs.submission.presentation.listsubmission

import android.content.Intent
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
import com.mjs.core.ui.submission.SubmissionAdapter
import com.mjs.submission.R
import com.mjs.submission.databinding.ActivityListSubmissionBinding
import com.mjs.submission.di.submissionModule
import com.mjs.submission.presentation.detailsubmission.DetailSubmissionActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules

class ListSubmissionActivity :
    AppCompatActivity(),
    SubmissionAdapter.OnItemClickCallback {
    private lateinit var binding: ActivityListSubmissionBinding
    private val listSubmissionViewModel: ListSubmissionViewModel by viewModel()
    private lateinit var submissionAdapter: SubmissionAdapter
    private var assignmentId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loadKoinModules(submissionModule)
        binding = ActivityListSubmissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        assignmentId = intent.getLongExtra(EXTRA_ASSIGNMENT_ID, -1L)

        setupRecyclerView()
        checkDarkMode()
        observeSubmissions()

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        if (assignmentId != -1L) {
            listSubmissionViewModel.getSubmissionsByAssignment(assignmentId)
        } else {
            binding.pbSubmissions.visibility = View.GONE
            binding.tvNoSubmissions.visibility = View.VISIBLE
            binding.tvNoSubmissions.text = getString(R.string.error_loading_submissions)
        }
    }

    private fun setupRecyclerView() {
        submissionAdapter = SubmissionAdapter()
        submissionAdapter.setOnItemClickCallback(this)
        binding.rvSubmissions.apply {
            layoutManager = LinearLayoutManager(this@ListSubmissionActivity)
            adapter = submissionAdapter
        }
    }

    override fun onItemClicked(submissionId: Long) {
        val intent = Intent(this, DetailSubmissionActivity::class.java)
        intent.putExtra(DetailSubmissionActivity.Companion.EXTRA_SUBMISSION_ID, submissionId)
        startActivity(intent)
    }

    private fun observeSubmissions() {
        listSubmissionViewModel.submissions.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.pbSubmissions.visibility = View.VISIBLE
                    binding.tvNoSubmissions.visibility = View.GONE
                    binding.ivNoSubmission.visibility = View.GONE
                    binding.rvSubmissions.visibility = View.GONE
                }

                is Resource.Success -> {
                    binding.pbSubmissions.visibility = View.GONE
                    val submissionsData = resource.data
                    if (submissionsData.isNullOrEmpty()) {
                        binding.tvNoSubmissions.visibility = View.VISIBLE
                        binding.ivNoSubmission.visibility = View.VISIBLE
                        binding.rvSubmissions.visibility = View.GONE
                    } else {
                        binding.tvNoSubmissions.visibility = View.GONE
                        binding.ivNoSubmission.visibility = View.GONE
                        binding.rvSubmissions.visibility = View.VISIBLE
                        submissionAdapter.submitList(submissionsData)
                    }
                }

                is Resource.Error -> {
                    binding.pbSubmissions.visibility = View.GONE
                    binding.tvNoSubmissions.visibility = View.VISIBLE
                    binding.ivNoSubmission.visibility = View.VISIBLE
                    binding.rvSubmissions.visibility = View.GONE
                    binding.tvNoSubmissions.text =
                        resource.message ?: getString(R.string.error_loading_submissions)
                    Toast
                        .makeText(
                            this,
                            resource.message
                                ?: getString(R.string.error_loading_submissions),
                            Toast.LENGTH_LONG,
                        ).show()
                }
            }
        }
    }

    private fun checkDarkMode() {
        listSubmissionViewModel.getThemeSetting.observe(this) {
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
