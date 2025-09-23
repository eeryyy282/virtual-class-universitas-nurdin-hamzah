package com.mjs.lecturer.presentation.assignment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.mjs.assignment.presentation.detailassignment.DetailAssignmentActivity
import com.mjs.core.data.Resource
import com.mjs.core.ui.assignment.AssignmentAdapterLecturerCategorized
import com.mjs.lecturer.databinding.FragmentAssignmentBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class AssignmentFragment : Fragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentAssignmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AssignmentViewModel by viewModel()
    private lateinit var assignmentAdapter: AssignmentAdapterLecturerCategorized

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAssignmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeAssignments()
    }

    private fun setupRecyclerView() {
        assignmentAdapter = AssignmentAdapterLecturerCategorized()
        assignmentAdapter.getCourseName = { courseId ->
            viewModel.getCourseNameById(courseId)
        }
        assignmentAdapter.getCoursePhotoProfile = { courseId ->
            viewModel.getCoursePhotoProfileById(courseId)
        }
        assignmentAdapter.onItemClick = { assignment ->
            val uri = "assignment://detail_assignment_activity".toUri()
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.putExtra(DetailAssignmentActivity.EXTRA_ASSIGNMENT_ID, assignment.assignmentId)
            startActivity(intent)
        }
        binding.rvAssignmentLecturer.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = assignmentAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeAssignments() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.assignments.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.progressBarAssignmentList.visibility = View.VISIBLE
                            binding.rvAssignmentLecturer.visibility = View.GONE
                            binding.tvDoesntHaveAnAssignment.visibility = View.GONE
                            binding.ivDoesntHaveAnAssignment.visibility = View.GONE
                        }

                        is Resource.Success -> {
                            binding.progressBarAssignmentList.visibility = View.GONE
                            val (activeAssignments, pastDeadlineAssignments) =
                                resource.data
                                    ?: Pair(emptyList(), emptyList())
                            if (activeAssignments.isEmpty() && pastDeadlineAssignments.isEmpty()) {
                                binding.rvAssignmentLecturer.visibility = View.GONE
                                binding.tvDoesntHaveAnAssignment.visibility = View.VISIBLE
                                binding.ivDoesntHaveAnAssignment.visibility = View.VISIBLE
                                binding.tvDoesntHaveAnAssignment.text =
                                    getString(com.mjs.core.R.string.doesnt_have_an_assignment_text)
                            } else {
                                assignmentAdapter.setData(
                                    activeAssignments,
                                    pastDeadlineAssignments,
                                )
                                binding.rvAssignmentLecturer.visibility = View.VISIBLE
                                binding.tvDoesntHaveAnAssignment.visibility = View.GONE
                                binding.ivDoesntHaveAnAssignment.visibility = View.GONE
                            }
                        }

                        is Resource.Error -> {
                            binding.progressBarAssignmentList.visibility = View.GONE
                            binding.rvAssignmentLecturer.visibility = View.GONE
                            binding.tvDoesntHaveAnAssignment.text = resource.message
                                ?: getString(com.mjs.core.R.string.error_loading_assignments)
                            binding.tvDoesntHaveAnAssignment.visibility = View.VISIBLE
                            binding.ivDoesntHaveAnAssignment.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvAssignmentLecturer.adapter = null
        _binding = null
    }
}
