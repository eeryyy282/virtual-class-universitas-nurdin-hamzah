package com.mjs.mahasiswa.presentation.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mjs.core.data.Resource
import com.mjs.core.ui.TaskHomeAdapter
import com.mjs.mahasiswa.R
import com.mjs.mahasiswa.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModel()
    private lateinit var taskHomeAdapter: TaskHomeAdapter

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
        setupProfileUser()
        setupRecyclerViewTugas()
        observeTugasList()
        observeAttendanceStreak()
        observeTodaySchedule()
    }

    private fun setupProfileUser() {
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.mahasiswaData.collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        val mahasiswa = resource.data
                        if (mahasiswa != null) {
                            binding.tvNameHome.text = mahasiswa.nama
                            binding.tvIdUserHome.text = mahasiswa.nim
                            binding.tvMentorUserHome.text = mahasiswa.dosenPembimbing
                        } else {
                            Toast
                                .makeText(
                                    context,
                                    "Gagal memuat data mahasiswa",
                                    Toast.LENGTH_SHORT,
                                ).show()
                        }
                    }

                    is Resource.Error -> {
                        Toast
                            .makeText(
                                context,
                                resource.message ?: "Terjadi kesalahan saat memuat profil",
                                Toast.LENGTH_SHORT,
                            ).show()
                    }

                    null -> {
                    }
                }
            }
        }
    }

    private fun setupRecyclerViewTugas() {
        taskHomeAdapter = TaskHomeAdapter()
        // Asumsi binding.rvTaskHome adalah RecyclerView untuk tugas
        binding.rvTaskHome.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = taskHomeAdapter
            setHasFixedSize(true)
        }
        taskHomeAdapter.getClassName = { kelasId ->
            homeViewModel.getClassNameById(kelasId)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeTugasList() {
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.tugasListState.collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        binding.progressBarTaskHome.visibility = View.VISIBLE
                        binding.tvDoesntHaveAnTask.visibility = View.GONE
                        binding.ivDoesntHaveAnTask.visibility = View.GONE
                        binding.rvTaskHome.visibility = View.GONE
                    }

                    is Resource.Success -> {
                        val tugasList = resource.data
                        binding.progressBarTaskHome.visibility = View.GONE
                        if (tugasList.isNullOrEmpty()) {
                            binding.tvDoesntHaveAnTask.visibility = View.VISIBLE
                            binding.ivDoesntHaveAnTask.visibility = View.VISIBLE
                            binding.rvTaskHome.visibility = View.GONE
                        } else {
                            binding.tvDoesntHaveAnTask.visibility = View.GONE
                            binding.ivDoesntHaveAnTask.visibility = View.GONE
                            binding.rvTaskHome.visibility = View.VISIBLE
                            taskHomeAdapter.setData(tugasList)
                        }
                    }

                    is Resource.Error -> {
                        binding.progressBarTaskHome.visibility = View.GONE
                        binding.tvDoesntHaveAnTask.visibility = View.VISIBLE
                        binding.ivDoesntHaveAnTask.visibility = View.VISIBLE
                        binding.rvTaskHome.visibility = View.GONE
                    }

                    null -> {
                        binding.progressBarTaskHome.visibility = View.GONE
                        binding.tvDoesntHaveAnTask.visibility = View.VISIBLE
                        binding.ivDoesntHaveAnTask.visibility = View.VISIBLE
                        binding.rvTaskHome.visibility = View.GONE
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.enrolledCoursesMapState.collectLatest { map ->
                taskHomeAdapter.notifyDataSetChanged() // Ini mungkin perlu disesuaikan jika map memengaruhi tampilan tugas
            }
        }
    }

    private fun observeAttendanceStreak() {
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.attendanceStreakState.collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        binding.ivDoesntHaveAnTask.visibility =
                            View.GONE // Sesuaikan dengan UI Anda
                        binding.tvDoesntHaveAnTask.visibility =
                            View.GONE // Sesuaikan dengan UI Anda
                        binding.progressBarStatistic.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        val streakCount = resource.data
                        binding.ivDoesntHaveAnTask.visibility =
                            View.GONE // Sesuaikan dengan UI Anda
                        binding.tvDoesntHaveAnTask.visibility =
                            View.GONE // Sesuaikan dengan UI Anda
                        binding.progressBarStatistic.visibility = View.GONE
                        binding.ivFireIconStatistic.visibility = View.VISIBLE
                        binding.tvStreakStatistic.visibility = View.VISIBLE
                        binding.tvStreakStatisticCount.visibility = View.VISIBLE
                        binding.tvStreakStatisticCount.text = streakCount?.toString() ?: "0"
                        if ((streakCount ?: 0) > 10) {
                            binding.ivFireIconStatistic.setColorFilter(
                                ContextCompat.getColor(requireContext(), R.color.air_force_blue),
                            )
                        } else {
                            binding.ivFireIconStatistic.clearColorFilter()
                        }
                    }

                    is Resource.Error -> {
                        binding.ivDoesntHaveAnTask.visibility =
                            View.VISIBLE // Sesuaikan dengan UI Anda
                        binding.tvDoesntHaveAnTask.visibility =
                            View.VISIBLE // Sesuaikan dengan UI Anda
                        binding.progressBarStatistic.visibility = View.GONE
                        Toast
                            .makeText(
                                context,
                                resource.message ?: "Gagal memuat data streak",
                                Toast.LENGTH_SHORT,
                            ).show()
                    }

                    null -> {
                    }
                }
            }
        }
    }

    private fun observeTodaySchedule() {
        viewLifecycleOwner.lifecycleScope.launch {
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
                            binding.ivDoesntHaveAnSchedule.visibility = View.GONE
                            binding.tvDoesntHaveAnSchedule.visibility = View.GONE
                            binding.tvScheduleClassroom.visibility = View.VISIBLE
                            binding.tvTimeScheduleHome.visibility = View.VISIBLE
                            binding.tvSubjectScheduleHome.visibility = View.VISIBLE
                            binding.btnScheduleDetailHome.visibility = View.VISIBLE
                            binding.tvScheduleClassroom.text = scheduleList[0].ruang
                            binding.tvTimeScheduleHome.text =
                                scheduleList[0]
                                    .jadwal
                                    .split(",")
                                    .getOrNull(1)
                                    ?.trim()
                            binding.tvSubjectScheduleHome.text = scheduleList[0].namaKelas
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvTaskHome.adapter = null
        _binding = null
    }
}
