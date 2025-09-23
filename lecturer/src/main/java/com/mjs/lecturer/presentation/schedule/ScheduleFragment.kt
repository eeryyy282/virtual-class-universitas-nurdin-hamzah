package com.mjs.lecturer.presentation.schedule

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.mjs.core.ui.schedule.ScheduleAdapter
import com.mjs.course.presentation.registered.DetailCourseRegisteredActivity
import com.mjs.lecturer.databinding.FragmentScheduleBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Suppress("DEPRECATION")
class ScheduleFragment : Fragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!
    private val scheduleViewModel: ScheduleViewModel by viewModel()
    private lateinit var scheduleAdapter: ScheduleAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeScheduleData()
    }

    private fun setupRecyclerView() {
        scheduleAdapter = ScheduleAdapter()
        scheduleAdapter.isForLecturerView = true
        binding.rvScheduleLecturer.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = scheduleAdapter
        }

        scheduleAdapter.onItemClick = { course ->
            val uri = "detail_course://detail_course_registered_activity".toUri()
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.putExtra(DetailCourseRegisteredActivity.EXTRA_COURSE_ID, course.courseId)
            startActivity(intent)
        }
    }

    private fun observeScheduleData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                scheduleViewModel.schedule.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.progressBarSchedule.visibility = View.VISIBLE
                            binding.rvScheduleLecturer.visibility = View.GONE
                            binding.ivDoesntHaveAnSchedule.visibility = View.GONE
                            binding.tvDoesntHaveAnSchedule.visibility = View.GONE
                        }

                        is Resource.Success -> {
                            binding.progressBarSchedule.visibility = View.GONE
                            val scheduleList = resource.data
                            if (scheduleList != null && scheduleList.isNotEmpty()) {
                                Log.d(
                                    "LecturerScheduleFragment",
                                    "Raw scheduleList size: ${scheduleList.size}",
                                )
                                Log.d(
                                    "LecturerScheduleFragment",
                                    "Raw scheduleList content: $scheduleList",
                                )

                                val groupedScheduleUnsorted =
                                    scheduleList
                                        .groupBy { course ->
                                            SimpleDateFormat("EEEE", Locale("id", "ID")).format(
                                                course.schedule,
                                            )
                                        }.map { entry -> Pair(entry.key, entry.value) }

                                val dayOrder =
                                    listOf(
                                        "Senin",
                                        "Selasa",
                                        "Rabu",
                                        "Kamis",
                                        "Jumat",
                                        "Sabtu",
                                        "Minggu",
                                    )
                                val groupedSchedule =
                                    groupedScheduleUnsorted.sortedWith(
                                        compareBy { (day, _) ->
                                            val index = dayOrder.indexOf(day)
                                            if (index == -1) Int.MAX_VALUE else index
                                        },
                                    )

                                Log.d(
                                    "LecturerScheduleFragment",
                                    "Grouped schedule size: ${groupedSchedule.size}",
                                )
                                Log.d(
                                    "LecturerScheduleFragment",
                                    "Grouped schedule content: $groupedSchedule",
                                )
                                scheduleAdapter.setData(groupedSchedule)
                                binding.rvScheduleLecturer.visibility = View.VISIBLE
                                binding.ivDoesntHaveAnSchedule.visibility = View.GONE
                                binding.tvDoesntHaveAnSchedule.visibility = View.GONE
                            } else {
                                binding.rvScheduleLecturer.visibility = View.GONE
                                binding.ivDoesntHaveAnSchedule.visibility = View.VISIBLE
                                binding.tvDoesntHaveAnSchedule.visibility = View.VISIBLE
                            }
                        }

                        is Resource.Error -> {
                            binding.progressBarSchedule.visibility = View.GONE
                            binding.rvScheduleLecturer.visibility = View.GONE
                            binding.ivDoesntHaveAnSchedule.visibility = View.VISIBLE
                            binding.tvDoesntHaveAnSchedule.visibility = View.VISIBLE
                            Toast
                                .makeText(
                                    requireContext(),
                                    resource.message ?: "An error occurred",
                                    Toast.LENGTH_LONG,
                                ).show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvScheduleLecturer.adapter = null
        _binding = null
    }
}
