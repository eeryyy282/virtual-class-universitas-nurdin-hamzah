package com.mjs.mahasiswa.presentation.schedule

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.mjs.core.data.Resource
import com.mjs.core.ui.schedule.ScheduleAdapter
import com.mjs.mahasiswa.R
import com.mjs.mahasiswa.databinding.FragmentScheduleBinding
import kotlinx.coroutines.flow.collectLatest
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
    }

    private fun setupRecyclerView() {
        scheduleAdapter = ScheduleAdapter()
        scheduleAdapter.isForDosenView = false
        binding.rvSchedule.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = scheduleAdapter
        }

        scheduleAdapter.getDosenName = { nidnString ->
            val currentDosenNamesMap = scheduleViewModel.dosenNamesMap.value
            val nidnInt = nidnString.toIntOrNull()
            if (nidnInt != null) {
                currentDosenNamesMap[nidnInt]
                    ?: nidnString
            } else {
                nidnString
            }
        }

        scheduleAdapter.onItemClick = { kelas ->
            Toast
                .makeText(requireContext(), "Clicked on ${kelas.namaKelas}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeScheduleData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                scheduleViewModel.scheduleForUi.collectLatest { resource ->
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
                            if (!scheduleList.isNullOrEmpty()) {
                                val groupedScheduleUnsorted =
                                    scheduleList
                                        .groupBy { kelas ->
                                            kelas.jadwal
                                                .split(",")
                                                .firstOrNull()
                                                ?.trim() ?: getString(R.string.unknown_day)
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

                                scheduleAdapter.setData(groupedSchedule)
                                binding.rvSchedule.visibility = View.VISIBLE
                                binding.ivDoesntHaveAnSchedule.visibility = View.GONE
                                binding.tvDoesntHaveAnSchedule.visibility = View.GONE
                            } else {
                                binding.rvSchedule.visibility = View.GONE
                                binding.ivDoesntHaveAnSchedule.visibility = View.VISIBLE
                                binding.tvDoesntHaveAnSchedule.visibility = View.VISIBLE
                                scheduleAdapter.setData(emptyList())
                            }
                        }

                        is Resource.Error -> {
                            binding.progressBarSchedule.visibility = View.GONE
                            binding.rvSchedule.visibility = View.GONE
                            binding.ivDoesntHaveAnSchedule.visibility = View.VISIBLE
                            binding.tvDoesntHaveAnSchedule.visibility = View.VISIBLE
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
                scheduleViewModel.dosenNamesMap.collectLatest { _ ->
                    if (::scheduleAdapter.isInitialized && binding.rvSchedule.adapter != null) {
                        scheduleAdapter.notifyDataSetChanged()
                    }
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
