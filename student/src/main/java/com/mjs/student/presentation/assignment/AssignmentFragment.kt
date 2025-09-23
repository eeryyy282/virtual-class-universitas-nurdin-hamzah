package com.mjs.student.presentation.assignment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.mjs.assignment.presentation.detailassignment.DetailAssignmentActivity
import com.mjs.core.data.Resource
import com.mjs.core.ui.assignment.AssignmentAdapterStudent
import com.mjs.student.R
import com.mjs.student.databinding.FragmentAssignmentBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class AssignmentFragment : Fragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentAssignmentBinding? = null
    private val binding get() = _binding!!

    private val assignmentViewModel: AssignmentViewModel by viewModel()
    private lateinit var assignmentAdapter: AssignmentAdapterStudent

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
        setupHistoryButton()
    }

    private fun setupRecyclerView() {
        assignmentAdapter = AssignmentAdapterStudent()
        assignmentAdapter.getCoursesName = {
            assignmentViewModel.getCourseName(it)
        }
        assignmentAdapter.getCoursesPhotoProfile = {
            assignmentViewModel.getCourseImageUrl(it)
        }
        assignmentAdapter.onItemClick = { assignment ->
            val uri = "assignment://detail_assignment_activity".toUri()
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.putExtra(DetailAssignmentActivity.EXTRA_ASSIGNMENT_ID, assignment.assignmentId)
            startActivity(intent)
        }
        binding.recyclerViewAssignmentList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = assignmentAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeAssignments() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                assignmentViewModel.assignments.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.progressBarAssignmentLoading.visibility = View.VISIBLE
                            binding.recyclerViewAssignmentList.visibility = View.GONE
                            binding.textViewNoAssignmentMessage.visibility = View.GONE
                            binding.imageViewNoAssignment.visibility = View.GONE
                        }

                        is Resource.Success -> {
                            binding.progressBarAssignmentLoading.visibility = View.GONE
                            val (notFinishedAssignments, lateAssignments) =
                                resource.data ?: Pair(emptyList(), emptyList())

                            if (notFinishedAssignments.isEmpty() && lateAssignments.isEmpty()) {
                                binding.recyclerViewAssignmentList.visibility = View.GONE
                                binding.textViewNoAssignmentMessage.visibility = View.VISIBLE
                                binding.imageViewNoAssignment.visibility = View.VISIBLE
                                binding.textViewNoAssignmentMessage.text =
                                    getString(R.string.no_assignment_message)
                            } else {
                                assignmentAdapter.setData(notFinishedAssignments, lateAssignments)
                                binding.recyclerViewAssignmentList.visibility = View.VISIBLE
                                binding.textViewNoAssignmentMessage.visibility = View.GONE
                                binding.imageViewNoAssignment.visibility = View.GONE
                            }
                        }

                        is Resource.Error -> {
                            binding.progressBarAssignmentLoading.visibility = View.GONE
                            binding.recyclerViewAssignmentList.visibility = View.GONE
                            binding.textViewNoAssignmentMessage.text =
                                resource.message ?: getString(R.string.error_load_assignment)
                            binding.textViewNoAssignmentMessage.visibility = View.VISIBLE
                            binding.imageViewNoAssignment.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun setupHistoryButton() {
        binding.cardViewAssignmentHistory.setOnClickListener {
            Toast
                .makeText(
                    requireContext(),
                    "Fitur riwayat tugas belum diimplementasi",
                    Toast.LENGTH_SHORT,
                ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerViewAssignmentList.adapter = null
        _binding = null
    }
}
