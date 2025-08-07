package com.mjs.dosen.presentation.home

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
        setupProfileDosen()
        setupRecyclerViewTugasDosen()
        observeTugasDosen()
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
                            binding.tvIdUserHome.text = dosen.nidn
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
            homeViewModel.kelasDosenMapState.collectLatest {
                if (::taskHomeAdapter.isInitialized) {
                    taskHomeAdapter.notifyDataSetChanged()
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
