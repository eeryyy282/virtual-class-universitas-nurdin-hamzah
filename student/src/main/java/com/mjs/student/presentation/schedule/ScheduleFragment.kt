package com.mjs.student.presentation.schedule

import android.annotation.SuppressLint
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
import com.mjs.core.ui.schedule.ScheduleAdapter
import com.mjs.course.presentation.registered.DetailCourseRegisteredActivity
import com.mjs.student.R
import com.mjs.student.databinding.FragmentScheduleBinding
import kotlinx.coroutines.flow.collectLatest
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
        scheduleAdapter.isForLecturerView = false
        binding.recyclerViewScheduleList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = scheduleAdapter
            setHasFixedSize(true)
        }

        scheduleAdapter.getLecturerNameById = { lecturerId: Long ->
            val currentLecturerNamesMap = scheduleViewModel.lecturerNamesMap.value
            currentLecturerNamesMap[lecturerId] ?: lecturerId.toString()
        }

        scheduleAdapter.onItemClick = { course ->
            val uri = "detail_course://detail_course_registered_activity".toUri()
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.putExtra(DetailCourseRegisteredActivity.EXTRA_COURSE_ID, course.courseId)
            startActivity(intent)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeScheduleData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                scheduleViewModel.scheduleListState.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.progressBarScheduleLoading.visibility = View.VISIBLE
                            binding.recyclerViewScheduleList.visibility = View.GONE
                            binding.imageViewNoSchedule.visibility = View.GONE
                            binding.textViewNoScheduleMessage.visibility = View.GONE
                        }

                        is Resource.Success -> {
                            binding.progressBarScheduleLoading.visibility = View.GONE
                            val scheduleList = resource.data
                            if (!scheduleList.isNullOrEmpty()) {
                                val sdf = SimpleDateFormat("EEEE", Locale("in", "ID"))
                                val groupedScheduleUnsorted =
                                    scheduleList
                                        .groupBy { course ->
                                            try {
                                                sdf.format(course.schedule)
                                            } catch (_: Exception) {
                                                getString(R.string.unknown_day)
                                            }
                                        }.map { entry ->
                                            Pair(entry.key, entry.value)
                                        }
                                val dayOrder =
                                    listOf(
                                        getString(R.string.monday),
                                        getString(R.string.tuesday),
                                        getString(R.string.wednesday),
                                        getString(R.string.thursday),
                                        getString(R.string.friday),
                                        getString(R.string.saturday),
                                        getString(R.string.sunday),
                                    )
                                val groupedSchedule =
                                    groupedScheduleUnsorted.sortedWith(
                                        compareBy { (day, _) ->
                                            val index = dayOrder.indexOf(day)
                                            if (index == -1) Int.MAX_VALUE else index
                                        },
                                    )

                                scheduleAdapter.setData(groupedSchedule)
                                binding.recyclerViewScheduleList.visibility = View.VISIBLE
                                binding.imageViewNoSchedule.visibility = View.GONE
                                binding.textViewNoScheduleMessage.visibility = View.GONE
                            } else {
                                binding.recyclerViewScheduleList.visibility = View.GONE
                                binding.imageViewNoSchedule.visibility = View.VISIBLE
                                binding.textViewNoScheduleMessage.visibility = View.VISIBLE
                                binding.textViewNoScheduleMessage.text =
                                    getString(R.string.no_schedule_message)
                                scheduleAdapter.setData(emptyList())
                            }
                        }

                        is Resource.Error -> {
                            binding.progressBarScheduleLoading.visibility = View.GONE
                            binding.recyclerViewScheduleList.visibility = View.GONE
                            binding.imageViewNoSchedule.visibility = View.VISIBLE
                            binding.textViewNoScheduleMessage.visibility = View.VISIBLE
                            binding.textViewNoScheduleMessage.text =
                                resource.message ?: getString(R.string.no_schedule_message)
                            scheduleAdapter.setData(emptyList())
                            Toast
                                .makeText(
                                    requireContext(),
                                    resource.message ?: getString(R.string.error_occurred),
                                    Toast.LENGTH_LONG,
                                ).show()
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                scheduleViewModel.lecturerNamesMap.collectLatest { _ ->
                    if (::scheduleAdapter.isInitialized && binding.recyclerViewScheduleList.adapter != null) {
                        scheduleAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerViewScheduleList.adapter = null
        _binding = null
    }
}
