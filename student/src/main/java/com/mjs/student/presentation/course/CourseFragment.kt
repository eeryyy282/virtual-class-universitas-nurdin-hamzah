package com.mjs.student.presentation.course

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.mjs.core.data.Resource
import com.mjs.core.ui.course.CourseAdapterStudent
import com.mjs.course.presentation.registered.DetailCourseRegisteredActivity
import com.mjs.student.R
import com.mjs.student.databinding.FragmentCourseBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CourseFragment : Fragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentCourseBinding? = null
    internal val binding get() = _binding!!

    private val courseViewModel: CourseViewModel by viewModel()
    private lateinit var courseAdapter: CourseAdapterStudent

    private val detailCourseLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            if (result.resultCode == DetailCourseRegisteredActivity.RESULT_REFRESH_REQUESTED) {
                courseViewModel.triggerRefresh()
            }
        }

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
        observeEnrolledCourses()
        observeLecturerNamesMap()

        binding.buttonEnrollCourse.setOnClickListener {
            val uri = "enroll_class://enroll_class_activity".toUri()
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        courseAdapter = CourseAdapterStudent()
        binding.recyclerViewCourseList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = courseAdapter
            setHasFixedSize(true)
        }

        courseAdapter.getLecturerNameById = { lecturerId ->
            courseViewModel.lecturerNamesMap.value[lecturerId]
                ?: lecturerId.toString()
        }

        courseAdapter.onItemClick = { course ->
            val intent = Intent(requireContext(), DetailCourseRegisteredActivity::class.java)
            intent.putExtra(DetailCourseRegisteredActivity.EXTRA_COURSE_ID, course.courseId)
            detailCourseLauncher.launch(intent)
        }
    }

    private fun observeEnrolledCourses() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                courseViewModel.enrolledCourseListState.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.progressBarCourseLoading.visibility = View.VISIBLE
                            binding.recyclerViewCourseList.visibility = View.GONE
                            binding.imageViewNoCourse.visibility = View.GONE
                            binding.textViewNoCourseMessage.visibility = View.GONE
                        }

                        is Resource.Success -> {
                            binding.progressBarCourseLoading.visibility = View.GONE
                            val courses = resource.data
                            if (courses.isNullOrEmpty()) {
                                binding.recyclerViewCourseList.visibility = View.GONE
                                binding.imageViewNoCourse.visibility = View.VISIBLE
                                binding.textViewNoCourseMessage.visibility = View.VISIBLE
                                binding.textViewNoCourseMessage.text =
                                    getString(R.string.no_courses_message)
                                binding.textViewTotalCoursesCount.text = "0"
                            } else {
                                binding.recyclerViewCourseList.visibility = View.VISIBLE
                                binding.imageViewNoCourse.visibility = View.GONE
                                binding.textViewNoCourseMessage.visibility = View.GONE
                                courseAdapter.submitList(courses)
                                binding.textViewTotalCoursesCount.text = courses.size.toString()
                            }
                        }

                        is Resource.Error -> {
                            binding.progressBarCourseLoading.visibility = View.GONE
                            binding.recyclerViewCourseList.visibility = View.GONE
                            binding.imageViewNoCourse.visibility = View.VISIBLE
                            binding.textViewNoCourseMessage.visibility = View.VISIBLE
                            binding.textViewNoCourseMessage.text =
                                resource.message ?: getString(R.string.failed_to_load_class)
                            Toast
                                .makeText(
                                    requireContext(),
                                    resource.message ?: getString(R.string.trouble_error),
                                    Toast.LENGTH_LONG,
                                ).show()
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeLecturerNamesMap() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                courseViewModel.lecturerNamesMap.collect {
                    if (::courseAdapter.isInitialized && binding.recyclerViewCourseList.adapter != null) {
                        courseAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerViewCourseList.adapter = null
        _binding = null
    }
}
