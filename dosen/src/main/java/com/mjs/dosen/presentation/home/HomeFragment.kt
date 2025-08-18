package com.mjs.dosen.presentation.home

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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.mjs.core.data.Resource
import com.mjs.core.ui.task.TaskHomeAdapter
import com.mjs.detailclass.registered.DetailClassRegisteredActivity
import com.mjs.detailtask.presentation.DetailTaskActivity
import com.mjs.dosen.R
import com.mjs.dosen.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentHomeBinding? = null
    internal val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModel()
    private lateinit var taskHomeAdapter: TaskHomeAdapter

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
        setupProfileDosen()
        setupRecyclerViewTugasDosen()
        observeTugasDosen()
        observeTodaySchedule()
        setupTaskNavigation()
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

    private fun setupTaskNavigation() {
        binding.ibtnDetailTask.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_navigation_task)
        }
    }

    private fun setupProfileDosen() {
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.dosenData.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        val dosen = it.data
                        if (dosen != null) {
                            binding.tvNameHome.text = dosen.nama
                            binding.tvIdUserHome.text = dosen.nidn.toString()
                            Glide
                                .with(requireContext())
                                .load(dosen.fotoProfil)
                                .placeholder(R.drawable.profile_photo)
                                .error(R.drawable.profile_photo)
                                .into(binding.ivProfileUser)
                        } else {
                            Toast
                                .makeText(context, "Gagal memuat data dosen", Toast.LENGTH_SHORT)
                                .show()
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

    private fun setupRecyclerViewTugasDosen() {
        taskHomeAdapter = TaskHomeAdapter()
        binding.rvTaskHome.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = taskHomeAdapter
            setHasFixedSize(true)
        }
        taskHomeAdapter.getClassName = { kelasId ->
            homeViewModel.getNamaKelasById(kelasId)
        }
        taskHomeAdapter.getClassPhotoProfile = { kelasId ->
            homeViewModel.getKelasImageById(kelasId)
        }
        taskHomeAdapter.onItemClick = { tugas ->
            val uri = "detail_task://detail_task_activity".toUri()
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.putExtra(DetailTaskActivity.EXTRA_TASK, tugas)
            startActivity(intent)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeTugasDosen() {
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.tugasListDosenState.collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        binding.progressBarTaskHome.visibility =
                            View.VISIBLE
                        binding.tvDoesntHaveAnTask.visibility =
                            View.GONE
                        binding.ivDoesntHaveAnTask.visibility =
                            View.GONE
                        binding.rvTaskHome.visibility = View.GONE
                    }

                    is Resource.Success -> {
                        binding.progressBarTaskHome.visibility = View.GONE
                        val tugasList = resource.data
                        if (tugasList.isNullOrEmpty()) {
                            binding.tvDoesntHaveAnTask.visibility = View.VISIBLE
                            binding.ivDoesntHaveAnTask.visibility = View.VISIBLE
                            binding.rvTaskHome.visibility = View.GONE
                        } else {
                            binding.tvDoesntHaveAnTask.visibility = View.GONE
                            binding.ivDoesntHaveAnTask.visibility = View.GONE
                            binding.rvTaskHome.visibility = View.VISIBLE
                            taskHomeAdapter.submitList(tugasList)
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
            homeViewModel.kelasDosenMapState.collectLatest {
                if (::taskHomeAdapter.isInitialized) {
                    taskHomeAdapter.notifyDataSetChanged()
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
                            val todaySchedule = scheduleList[0]
                            binding.ivDoesntHaveAnSchedule.visibility = View.GONE
                            binding.tvDoesntHaveAnSchedule.visibility = View.GONE
                            binding.tvScheduleClassroom.visibility = View.VISIBLE
                            binding.tvTimeScheduleHome.visibility = View.VISIBLE
                            binding.tvSubjectScheduleHome.visibility = View.VISIBLE
                            binding.btnScheduleDetailHome.visibility = View.VISIBLE
                            binding.tvScheduleClassroom.text = todaySchedule.ruang
                            binding.tvTimeScheduleHome.text =
                                todaySchedule
                                    .jadwal
                                    .split(",")
                                    .getOrNull(1)
                                    ?.trim()
                            binding.tvSubjectScheduleHome.text = todaySchedule.namaKelas
                            binding.btnScheduleDetailHome.setOnClickListener {
                                val uri = "detail_class://detail_class_registered_activity".toUri()
                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                intent.putExtra(
                                    DetailClassRegisteredActivity.EXTRA_KELAS_ID,
                                    todaySchedule.kelasId,
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

    override fun onDestroyView() {
        super.onDestroyView()

        if (_binding != null && ::taskHomeAdapter.isInitialized) {
            binding.rvTaskHome.adapter = null
        }
        _binding = null
    }
}
