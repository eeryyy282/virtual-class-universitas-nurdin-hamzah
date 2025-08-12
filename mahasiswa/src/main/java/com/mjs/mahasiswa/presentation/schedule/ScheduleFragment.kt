package com.mjs.mahasiswa.presentation.schedule

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
import com.mjs.core.ui.schedule.ScheduleAdapter
import com.mjs.mahasiswa.databinding.FragmentScheduleBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScheduleFragment : Fragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!
    private val scheduleViewModel: ScheduleViewModel by viewModel()
    private lateinit var scheduleAdapter: ScheduleAdapter
    private val dosenNamesMap = mutableMapOf<String, String>()

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
        scheduleViewModel.getStudentSchedule()
    }

    private fun setupRecyclerView() {
        scheduleAdapter = ScheduleAdapter()
        scheduleAdapter.isForDosenView = false
        binding.rvSchedule.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = scheduleAdapter
        }

        scheduleAdapter.getDosenName = { nidn ->
            dosenNamesMap[nidn] ?: nidn
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
                    binding.rvSchedule.visibility = View.GONE
                    binding.ivDoesntHaveAnSchedule.visibility = View.GONE
                    binding.tvDoesntHaveAnSchedule.visibility = View.GONE
                }

                is Resource.Success -> {
                    binding.progressBarSchedule.visibility = View.GONE
                    val scheduleList = resource.data
                    if (scheduleList != null && scheduleList.isNotEmpty()) {
                        Log.d("ScheduleFragment", "Raw scheduleList size: ${scheduleList.size}")
                        Log.d("ScheduleFragment", "Raw scheduleList content: $scheduleList")
                        viewLifecycleOwner.lifecycleScope.launch {
                            dosenNamesMap.clear()
                            val uniqueNidns = scheduleList.map { it.nidn }.distinct()
                            uniqueNidns.forEach { nidn ->
                                dosenNamesMap[nidn.toString()] =
                                    scheduleViewModel.getDosenNameByNidn(nidn).first()
                            }
                            val groupedScheduleUnsorted =
                                scheduleList
                                    .groupBy { kelas ->
                                        kelas.jadwal
                                            .split(",")
                                            .firstOrNull()
                                            ?.trim() ?: "Unknown Day"
                                    }.map { entry ->
                                        Pair(entry.key, entry.value)
                                    }
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
                                "ScheduleFragment",
                                "Grouped schedule size: ${groupedSchedule.size}",
                            )
                            Log.d("ScheduleFragment", "Grouped schedule content: $groupedSchedule")
                            scheduleAdapter.setData(groupedSchedule)
                            binding.rvSchedule.visibility = View.VISIBLE
                            binding.ivDoesntHaveAnSchedule.visibility = View.GONE
                            binding.tvDoesntHaveAnSchedule.visibility = View.GONE
                        }
                    } else {
                        binding.rvSchedule.visibility = View.GONE
                        binding.ivDoesntHaveAnSchedule.visibility = View.VISIBLE
                        binding.tvDoesntHaveAnSchedule.visibility = View.VISIBLE
                    }
                }

                is Resource.Error -> {
                    binding.progressBarSchedule.visibility = View.GONE
                    binding.rvSchedule.visibility = View.GONE
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
        binding.rvSchedule.adapter = null
        _binding = null
    }
}
