package com.mjs.lecturer.presentation.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.mjs.assignment.presentation.detailassignment.DetailAssignmentActivity
import com.mjs.core.data.Resource
import com.mjs.core.ui.assignment.AssignmentHomeAdapter
import com.mjs.course.presentation.registered.DetailCourseRegisteredActivity
import com.mjs.lecturer.R
import com.mjs.lecturer.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class HomeFragment : Fragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentHomeBinding? = null
    internal val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModel()
    private lateinit var assignmentHomeAdapter: AssignmentHomeAdapter

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission(),
            ) { isGranted: Boolean ->
                if (isGranted) {
                    Toast
                        .makeText(
                            requireContext(),
                            "Izin notifikasi diberikan.",
                            Toast.LENGTH_SHORT,
                        ).show()
                } else {
                    Toast
                        .makeText(requireContext(), "Izin notifikasi ditolak.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        requestNotificationPermission()
        setupProfileLecturer()
        setupRecyclerViewAssignmentsLecturer()
        observeAssignmentsLecturer()
        observeTodaySchedule()
        setupAssignmentNavigation()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED -> {
                }

                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }

                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    private fun setupAssignmentNavigation() {
        binding.ibtnDetailAssignment.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_navigation_assignment)
        }
    }

    private fun setupProfileLecturer() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.lecturerData.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                        }

                        is Resource.Success -> {
                            val lecturer = it.data
                            if (lecturer != null) {
                                binding.tvNameHome.text = lecturer.name
                                binding.tvLecturerIdHome.text =
                                    lecturer.lecturerId.toString()
                                Glide
                                    .with(requireContext())
                                    .load(lecturer.profilePicture)
                                    .placeholder(R.drawable.profile_photo)
                                    .error(R.drawable.profile_photo)
                                    .into(binding.ivProfileLecturer)
                            } else {
                                Toast
                                    .makeText(
                                        context,
                                        "Gagal memuat data dosen",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                            }
                        }

                        is Resource.Error -> {
                            Toast
                                .makeText(
                                    context,
                                    it.message ?: "Terjadi kesalahan",
                                    Toast.LENGTH_SHORT,
                                ).show()
                        }

                        null -> {
                        }
                    }
                }
            }
        }
    }

    private fun setupRecyclerViewAssignmentsLecturer() {
        assignmentHomeAdapter = AssignmentHomeAdapter()
        binding.rvAssignmentHome.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = assignmentHomeAdapter
            setHasFixedSize(true)
        }
        assignmentHomeAdapter.getCourseName = { courseId ->
            homeViewModel.getCourseNameById(courseId)
        }
        assignmentHomeAdapter.getCoursePhotoProfile = { courseId ->
            homeViewModel.getCourseImageById(courseId)
        }
        assignmentHomeAdapter.onItemClick = { assignment ->
            val uri = "assignment://detail_assignment_activity".toUri()
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.putExtra(DetailAssignmentActivity.EXTRA_ASSIGNMENT_ID, assignment.assignmentId)
            startActivity(intent)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeAssignmentsLecturer() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.assignmentListLecturerState.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.progressBarAssignmentHome.visibility =
                                View.VISIBLE
                            binding.tvDoesntHaveAnAssignment.visibility =
                                View.GONE
                            binding.ivDoesntHaveAnAssignment.visibility =
                                View.GONE
                            binding.rvAssignmentHome.visibility = View.GONE
                        }

                        is Resource.Success -> {
                            binding.progressBarAssignmentHome.visibility = View.GONE
                            val assignmentList = resource.data
                            if (assignmentList.isNullOrEmpty()) {
                                binding.tvDoesntHaveAnAssignment.visibility = View.VISIBLE
                                binding.ivDoesntHaveAnAssignment.visibility = View.VISIBLE
                                binding.rvAssignmentHome.visibility = View.GONE
                            } else {
                                binding.tvDoesntHaveAnAssignment.visibility = View.GONE
                                binding.ivDoesntHaveAnAssignment.visibility = View.GONE
                                binding.rvAssignmentHome.visibility = View.VISIBLE
                                assignmentHomeAdapter.submitList(assignmentList)
                            }
                        }

                        is Resource.Error -> {
                            binding.progressBarAssignmentHome.visibility = View.GONE
                            binding.tvDoesntHaveAnAssignment.visibility = View.VISIBLE
                            binding.ivDoesntHaveAnAssignment.visibility = View.VISIBLE
                            binding.rvAssignmentHome.visibility = View.GONE
                        }

                        null -> {
                            binding.progressBarAssignmentHome.visibility = View.GONE
                            binding.tvDoesntHaveAnAssignment.visibility = View.VISIBLE
                            binding.ivDoesntHaveAnAssignment.visibility = View.VISIBLE
                            binding.rvAssignmentHome.visibility = View.GONE
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.courseLecturerMapState.collectLatest {
                    if (::assignmentHomeAdapter.isInitialized) {
                        assignmentHomeAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun observeTodaySchedule() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.todayScheduleState.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.progressBarSchedule.visibility = View.VISIBLE
                            binding.ivDoesntHaveAnSchedule.visibility = View.GONE
                            binding.tvDoesntHaveAnSchedule.visibility = View.GONE
                            binding.tvScheduleClassroom.visibility = View.GONE
                            binding.tvTimeScheduleHome.visibility = View.GONE
                            binding.tvSubjectScheduleHome.visibility = View.GONE
                            binding.btnScheduleDetailHome.visibility = View.GONE
                        }

                        is Resource.Success -> {
                            binding.progressBarSchedule.visibility = View.GONE
                            val scheduleList = resource.data
                            if (scheduleList.isNullOrEmpty()) {
                                binding.ivDoesntHaveAnSchedule.visibility = View.VISIBLE
                                binding.tvDoesntHaveAnSchedule.visibility = View.VISIBLE
                                binding.tvScheduleClassroom.visibility = View.GONE
                                binding.tvTimeScheduleHome.visibility = View.GONE
                                binding.tvSubjectScheduleHome.visibility = View.GONE
                                binding.btnScheduleDetailHome.visibility = View.GONE
                            } else {
                                val todaySchedule = scheduleList[0]
                                binding.ivDoesntHaveAnSchedule.visibility = View.GONE
                                binding.tvDoesntHaveAnSchedule.visibility = View.GONE
                                binding.tvScheduleClassroom.visibility = View.VISIBLE
                                binding.tvTimeScheduleHome.visibility = View.VISIBLE
                                binding.tvSubjectScheduleHome.visibility = View.VISIBLE
                                binding.btnScheduleDetailHome.visibility = View.VISIBLE
                                binding.tvScheduleClassroom.text = todaySchedule.room
                                binding.tvTimeScheduleHome.text =
                                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(
                                        todaySchedule.schedule,
                                    )
                                binding.tvSubjectScheduleHome.text = todaySchedule.courseName
                                binding.btnScheduleDetailHome.setOnClickListener {
                                    val uri =
                                        "detail_course://detail_course_registered_activity".toUri()
                                    val intent = Intent(Intent.ACTION_VIEW, uri)
                                    intent.putExtra(
                                        DetailCourseRegisteredActivity.EXTRA_COURSE_ID,
                                        todaySchedule.courseId,
                                    )
                                    startActivity(intent)
                                }
                            }
                        }

                        is Resource.Error -> {
                            binding.progressBarSchedule.visibility = View.GONE
                            binding.ivDoesntHaveAnSchedule.visibility = View.VISIBLE
                            binding.tvDoesntHaveAnSchedule.visibility = View.VISIBLE
                            binding.tvScheduleClassroom.visibility = View.GONE
                            binding.tvTimeScheduleHome.visibility = View.GONE
                            binding.tvSubjectScheduleHome.visibility = View.GONE
                            binding.btnScheduleDetailHome.visibility = View.GONE
                        }

                        null -> {
                            binding.progressBarSchedule.visibility = View.GONE
                            binding.ivDoesntHaveAnSchedule.visibility = View.VISIBLE
                            binding.tvDoesntHaveAnSchedule.visibility = View.VISIBLE
                            binding.tvScheduleClassroom.visibility = View.GONE
                            binding.tvTimeScheduleHome.visibility = View.GONE
                            binding.tvSubjectScheduleHome.visibility = View.GONE
                            binding.btnScheduleDetailHome.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        if (_binding != null && ::assignmentHomeAdapter.isInitialized) {
            binding.rvAssignmentHome.adapter = null
        }
        _binding = null
    }
}
