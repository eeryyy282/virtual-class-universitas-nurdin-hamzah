package com.mjs.dosen.presentation.classroom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mjs.core.data.Resource
import com.mjs.core.ui.classroom.ClassroomAdapterDosen
import com.mjs.dosen.R
import com.mjs.dosen.databinding.FragmentClassroomBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ClassroomFragment : Fragment() {
    private var _binding: FragmentClassroomBinding? = null
    val binding get() = _binding!!

    private val classroomViewModel: ClassroomViewModel by viewModel()
    private lateinit var classroomAdapter: ClassroomAdapterDosen

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentClassroomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeClassroomData()
        observeTotalClasses()
    }

    private fun setupRecyclerView() {
        classroomAdapter = ClassroomAdapterDosen()
        binding.rvClassroom.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = classroomAdapter
            setHasFixedSize(true)
        }

        classroomAdapter.onItemClick = { kelas ->
            Toast
                .makeText(requireContext(), "Clicked on: ${kelas.namaKelas}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun observeClassroomData() {
        classroomViewModel.groupedClasses.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarClassroom.visibility = View.VISIBLE
                    binding.rvClassroom.visibility = View.GONE
                    binding.tvDoesntHaveAnClassroom.visibility = View.GONE
                    binding.ivDoesntHaveAnClassroom.visibility = View.GONE
                }

                is Resource.Success -> {
                    binding.progressBarClassroom.visibility = View.GONE
                    val groupedClasses = resource.data
                    if (groupedClasses.isNullOrEmpty()) {
                        binding.rvClassroom.visibility = View.GONE
                        binding.tvDoesntHaveAnClassroom.visibility = View.VISIBLE
                        binding.ivDoesntHaveAnClassroom.visibility = View.VISIBLE
                    } else {
                        binding.rvClassroom.visibility = View.VISIBLE
                        binding.tvDoesntHaveAnClassroom.visibility = View.GONE
                        binding.ivDoesntHaveAnClassroom.visibility = View.GONE
                        classroomAdapter.setData(groupedClasses)
                    }
                }

                is Resource.Error -> {
                    binding.progressBarClassroom.visibility = View.GONE
                    binding.rvClassroom.visibility = View.GONE
                    binding.tvDoesntHaveAnClassroom.visibility = View.VISIBLE
                    binding.ivDoesntHaveAnClassroom.visibility = View.VISIBLE
                    binding.tvDoesntHaveAnClassroom.text =
                        resource.message ?: getString(R.string.failed_to_load_class)
                    Toast
                        .makeText(
                            requireContext(),
                            resource.message ?: getString(R.string.error_throuble),
                            Toast.LENGTH_LONG,
                        ).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        classroomViewModel.fetchDosenClasses()
    }

    private fun observeTotalClasses() {
        classroomViewModel.totalClasses.observe(viewLifecycleOwner) { total ->
            binding.tvTotalClassJoined.text = total.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvClassroom.adapter = null
        _binding = null
    }
}
