package com.mjs.mahasiswa.presentation.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mjs.core.data.Resource
import com.mjs.core.ui.TaskHomeAdapter
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
        // Log.d(TAG, "onViewCreated CALLED")
        setupProfileUser()
        setupRecyclerViewTugas()
        observeTugasList()
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
                taskHomeAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvTaskHome.adapter = null
        _binding = null
    }
}
