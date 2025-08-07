package com.mjs.mahasiswa.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.mjs.core.data.Resource
import com.mjs.mahasiswa.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModel()

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
    }

    private fun setupProfileUser() {
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.mahasiswaData.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        val mahasiswa = it.data
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
