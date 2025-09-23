package com.mjs.lecturer.presentation.course

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
import com.mjs.core.data.Resource
import com.mjs.core.ui.course.CourseAdapterLecturer
import com.mjs.lecturer.R
import com.mjs.lecturer.databinding.FragmentCourseBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CourseFragment : Fragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentCourseBinding? = null
    private val binding get() = _binding!!

    private val courseViewModel: CourseViewModel by viewModel()
    private lateinit var courseAdapter: CourseAdapterLecturer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCourseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeCourseData()
        observeTotalCourses()
    }

    private fun setupRecyclerView() {
        courseAdapter = CourseAdapterLecturer()
        binding.rvCourse.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = courseAdapter
            setHasFixedSize(true)
        }

        courseAdapter.onItemClick = { course ->
            val uri = "detail_course://detail_course_registered_activity".toUri()
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.putExtra("courseId", course.courseId)
            startActivity(intent)
        }
    }

    private fun observeCourseData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                courseViewModel.groupedCourses.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.progressBarCourse.visibility = View.VISIBLE
                            binding.rvCourse.visibility = View.GONE
                            binding.tvDoesntHaveACourse.visibility = View.GONE
                            binding.ivDoesntHaveACourse.visibility = View.GONE
                        }

                        is Resource.Success -> {
                            binding.progressBarCourse.visibility = View.GONE
                            val groupedCoursesData = resource.data
                            if (groupedCoursesData.isNullOrEmpty()) {
                                binding.rvCourse.visibility = View.GONE
                                binding.tvDoesntHaveACourse.visibility = View.VISIBLE
                                binding.ivDoesntHaveACourse.visibility = View.VISIBLE
                            } else {
                                binding.rvCourse.visibility = View.VISIBLE
                                binding.tvDoesntHaveACourse.visibility = View.GONE
                                binding.ivDoesntHaveACourse.visibility = View.GONE
                                courseAdapter.setData(groupedCoursesData)
                            }
                        }

                        is Resource.Error -> {
                            binding.progressBarCourse.visibility = View.GONE
                            binding.rvCourse.visibility = View.GONE
                            binding.tvDoesntHaveACourse.visibility = View.VISIBLE
                            binding.ivDoesntHaveACourse.visibility = View.VISIBLE
                            binding.tvDoesntHaveACourse.text =
                                resource.message ?: getString(R.string.failed_to_load_course)
                            Toast
                                .makeText(
                                    requireContext(),
                                    resource.message ?: getString(R.string.error_throuble),
                                    Toast.LENGTH_LONG,
                                ).show()
                        }
                    }
                }
            }
        }
    }

    private fun observeTotalCourses() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                courseViewModel.totalCourses.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                        }

                        is Resource.Success -> {
                            binding.tvTotalCourseJoined.text = resource.data.toString()
                        }

                        is Resource.Error -> {
                            binding.tvTotalCourseJoined.text = "-"
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvCourse.adapter = null
        _binding = null
    }
}
