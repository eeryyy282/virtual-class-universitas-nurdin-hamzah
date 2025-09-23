package com.mjs.student.presentation.home

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
import com.mjs.student.R
import com.mjs.student.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class HomeFragment : Fragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

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
        setupUserProfile()
        setupAssignmentRecyclerView()
        observeAssignmentList()
        observeAttendanceStreak()
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
        binding.imageButtonDetailAssignment.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_navigation_assignment)
        }
    }

    private fun setupUserProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.studentProfileState.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                        }

                        is Resource.Success -> {
                            val student = resource.data
                            if (student != null) {
                                binding.textViewUserName.text = student.name
                                binding.textViewUserId.text = student.studentId.toString()
                                binding.textViewAcademicAdvisorName.text = student.supervisor
                                Glide
                                    .with(requireContext())
                                    .load(student.profilePicture)
                                    .placeholder(R.drawable.profile_photo)
                                    .error(R.drawable.profile_photo)
                                    .into(binding.imageViewUserProfile)
                            } else {
                                Toast
                                    .makeText(
                                        context,
                                        getString(R.string.failed_to_load_student_data),
                                        Toast.LENGTH_SHORT,
                                    ).show()
                            }
                        }

                        is Resource.Error -> {
                            Toast
                                .makeText(
                                    context,
                                    resource.message ?: getString(R.string.error_load_profile),
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

    private fun setupAssignmentRecyclerView() {
        assignmentHomeAdapter = AssignmentHomeAdapter()
        binding.recyclerViewAssignment.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = assignmentHomeAdapter
            setHasFixedSize(true)
        }
        assignmentHomeAdapter.getCourseName = {
            homeViewModel.getCourseNameById(it)
        }
        assignmentHomeAdapter.getCoursePhotoProfile = {
            homeViewModel.getCoursePhotoProfileById(it)
        }
        assignmentHomeAdapter.onItemClick = { assignment ->
            val uri = "assignment://detail_assignment_activity".toUri()
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.putExtra(DetailAssignmentActivity.EXTRA_ASSIGNMENT_ID, assignment.assignmentId)
            startActivity(intent)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeAssignmentList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.assignmentListState.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.progressBarAssignment.visibility = View.VISIBLE
                            binding.textViewNoAssignment.visibility = View.GONE
                            binding.imageViewNoAssignment.visibility = View.GONE
                            binding.recyclerViewAssignment.visibility = View.GONE
                        }

                        is Resource.Success -> {
                            val assignmentList = resource.data
                            binding.progressBarAssignment.visibility = View.GONE
                            if (assignmentList.isNullOrEmpty()) {
                                binding.textViewNoAssignment.visibility = View.VISIBLE
                                binding.imageViewNoAssignment.visibility = View.VISIBLE
                                binding.recyclerViewAssignment.visibility = View.GONE
                            } else {
                                binding.textViewNoAssignment.visibility = View.GONE
                                binding.imageViewNoAssignment.visibility = View.GONE
                                binding.recyclerViewAssignment.visibility = View.VISIBLE
                                assignmentHomeAdapter.submitList(assignmentList)
                            }
                        }

                        is Resource.Error -> {
                            binding.progressBarAssignment.visibility = View.GONE
                            binding.textViewNoAssignment.visibility = View.VISIBLE
                            binding.imageViewNoAssignment.visibility = View.VISIBLE
                            binding.recyclerViewAssignment.visibility = View.GONE
                            Toast
                                .makeText(
                                    context,
                                    resource.message ?: getString(R.string.error_load_assignment),
                                    Toast.LENGTH_SHORT,
                                ).show()
                        }

                        null -> {
                            binding.progressBarAssignment.visibility = View.GONE
                            binding.textViewNoAssignment.visibility = View.VISIBLE
                            binding.imageViewNoAssignment.visibility = View.VISIBLE
                            binding.recyclerViewAssignment.visibility = View.GONE
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.enrolledCoursesMapState.collectLatest {
                    if (::assignmentHomeAdapter.isInitialized) {
                        assignmentHomeAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun observeAttendanceStreak() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.attendanceStreakState.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.progressBarStatistic.visibility = View.VISIBLE
                            binding.imageViewStatisticFireIcon.visibility = View.GONE
                            binding.textViewStatisticStreak.visibility = View.GONE
                            binding.textViewStatisticStreakCount.visibility = View.GONE
                        }

                        is Resource.Success -> {
                            val streakCount = resource.data
                            binding.progressBarStatistic.visibility = View.GONE
                            binding.imageViewStatisticFireIcon.visibility = View.VISIBLE
                            binding.textViewStatisticStreak.visibility = View.VISIBLE
                            binding.textViewStatisticStreakCount.visibility = View.VISIBLE
                            binding.textViewStatisticStreakCount.text =
                                streakCount?.toString() ?: "0"
                            if ((streakCount ?: 0) > 10) {
                                binding.imageViewStatisticFireIcon.setColorFilter(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.air_force_blue,
                                    ),
                                )
                            } else {
                                binding.imageViewStatisticFireIcon.clearColorFilter()
                            }
                        }

                        is Resource.Error -> {
                            binding.progressBarStatistic.visibility = View.GONE
                            binding.imageViewStatisticFireIcon.visibility = View.GONE
                            binding.textViewStatisticStreak.visibility = View.GONE
                            binding.textViewStatisticStreakCount.visibility = View.GONE
                            Toast
                                .makeText(
                                    context,
                                    resource.message
                                        ?: getString(R.string.failed_to_load_streak_data),
                                    Toast.LENGTH_SHORT,
                                ).show()
                        }

                        null -> {
                            binding.progressBarStatistic.visibility = View.GONE
                            binding.imageViewStatisticFireIcon.visibility = View.GONE
                            binding.textViewStatisticStreak.visibility = View.GONE
                            binding.textViewStatisticStreakCount.visibility = View.GONE
                        }
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
                            binding.imageViewNoSchedule.visibility = View.GONE
                            binding.textViewNoSchedule.visibility = View.GONE
                            binding.textViewScheduleClassroom.visibility = View.GONE
                            binding.textViewScheduleTime.visibility = View.GONE
                            binding.textViewScheduleSubject.visibility = View.GONE
                            binding.buttonScheduleDetail.visibility = View.GONE
                        }

                        is Resource.Success -> {
                            binding.progressBarSchedule.visibility = View.GONE
                            val scheduleList = resource.data
                            if (scheduleList.isNullOrEmpty()) {
                                binding.imageViewNoSchedule.visibility = View.VISIBLE
                                binding.textViewNoSchedule.visibility = View.VISIBLE
                                binding.textViewScheduleClassroom.visibility = View.GONE
                                binding.textViewScheduleTime.visibility = View.GONE
                                binding.textViewScheduleSubject.visibility = View.GONE
                                binding.buttonScheduleDetail.visibility = View.GONE
                            } else {
                                val todaySchedule = scheduleList[0]
                                binding.imageViewNoSchedule.visibility = View.GONE
                                binding.textViewNoSchedule.visibility = View.GONE
                                binding.textViewScheduleClassroom.visibility = View.VISIBLE
                                binding.textViewScheduleTime.visibility = View.VISIBLE
                                binding.textViewScheduleSubject.visibility = View.VISIBLE
                                binding.buttonScheduleDetail.visibility = View.VISIBLE

                                binding.textViewScheduleClassroom.text = todaySchedule.room
                                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                                binding.textViewScheduleTime.text =
                                    timeFormat.format(todaySchedule.schedule)
                                binding.textViewScheduleSubject.text = todaySchedule.courseName
                                binding.buttonScheduleDetail.setOnClickListener {
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
                            binding.imageViewNoSchedule.visibility = View.VISIBLE
                            binding.textViewNoSchedule.visibility = View.VISIBLE
                            binding.textViewScheduleClassroom.visibility = View.GONE
                            binding.textViewScheduleTime.visibility = View.GONE
                            binding.textViewScheduleSubject.visibility = View.GONE
                            binding.buttonScheduleDetail.visibility = View.GONE
                            Toast
                                .makeText(
                                    context,
                                    resource.message ?: getString(R.string.error_load_schedule),
                                    Toast.LENGTH_SHORT,
                                ).show()
                        }

                        null -> {
                            binding.progressBarSchedule.visibility = View.GONE
                            binding.imageViewNoSchedule.visibility = View.VISIBLE
                            binding.textViewNoSchedule.visibility = View.VISIBLE
                            binding.textViewScheduleClassroom.visibility = View.GONE
                            binding.textViewScheduleTime.visibility = View.GONE
                            binding.textViewScheduleSubject.visibility = View.GONE
                            binding.buttonScheduleDetail.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (_binding != null && ::assignmentHomeAdapter.isInitialized) {
            binding.recyclerViewAssignment.adapter = null
        }
        _binding = null
    }
}
