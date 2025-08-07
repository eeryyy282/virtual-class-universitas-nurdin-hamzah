package com.mjs.dosen.presentation.schedule

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mjs.core.data.Resource
import com.mjs.core.ui.ScheduleAdapter
import com.mjs.dosen.databinding.FragmentScheduleBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

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
        scheduleViewModel.getDosenSchedule()
    }

    private fun setupRecyclerView() {
        scheduleAdapter = ScheduleAdapter()
        scheduleAdapter.isForDosenView = true
        binding.rvScheduleDosen.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = scheduleAdapter
        }

        scheduleAdapter.onItemClick = { kelas ->
            Toast
                .makeText(requireContext(), "Clicked on ${kelas.namaKelas}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun observeScheduleData() {
        scheduleViewModel.schedule.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarSchedule.visibility = View.VISIBLE
                    binding.rvScheduleDosen.visibility = View.GONE
                    binding.ivDoesntHaveAnSchedule.visibility = View.GONE
                    binding.tvDoesntHaveAnSchedule.visibility = View.GONE
                }

                is Resource.Success -> {
                    binding.progressBarSchedule.visibility = View.GONE
                    val scheduleList = resource.data
                    if (scheduleList != null && scheduleList.isNotEmpty()) {
                        Log.d(
                            "DosenScheduleFragment",
                            "Raw scheduleList size: ${scheduleList.size}",
                        )
                        Log.d("DosenScheduleFragment", "Raw scheduleList content: $scheduleList")

                        viewLifecycleOwner.lifecycleScope.launch {
                            val groupedScheduleUnsorted =
                                scheduleList
                                    .groupBy { kelas ->
                                        kelas.jadwal
                                            .split(",")
                                            .firstOrNull()
                                            ?.trim() ?: "Unknown Day"
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
                                "DosenScheduleFragment",
                                "Grouped schedule size: ${groupedSchedule.size}",
                            )
                            Log.d(
                                "DosenScheduleFragment",
                                "Grouped schedule content: $groupedSchedule",
                            )
                            scheduleAdapter.setData(groupedSchedule)
                            binding.rvScheduleDosen.visibility = View.VISIBLE
                            binding.ivDoesntHaveAnSchedule.visibility = View.GONE
                            binding.tvDoesntHaveAnSchedule.visibility = View.GONE
                        }
                    } else {
                        binding.rvScheduleDosen.visibility = View.GONE
                        binding.ivDoesntHaveAnSchedule.visibility = View.VISIBLE
                        binding.tvDoesntHaveAnSchedule.visibility = View.VISIBLE
                    }
                }

                is Resource.Error -> {
                    binding.progressBarSchedule.visibility = View.GONE
                    binding.rvScheduleDosen.visibility = View.GONE
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvScheduleDosen.adapter = null
        _binding = null
    }
}
