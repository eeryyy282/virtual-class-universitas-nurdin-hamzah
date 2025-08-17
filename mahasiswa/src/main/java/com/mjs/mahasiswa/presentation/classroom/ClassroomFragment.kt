package com.mjs.mahasiswa.presentation.classroom

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mjs.core.data.Resource
import com.mjs.core.ui.classroom.ClassroomAdapterMahasiswa
import com.mjs.mahasiswa.R
import com.mjs.mahasiswa.databinding.FragmentClassroomBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ClassroomFragment : Fragment() {
    private var _binding: FragmentClassroomBinding? = null
    val binding get() = _binding!!

    private val classroomViewModel: ClassroomViewModel by viewModel()
    private lateinit var classroomAdapter: ClassroomAdapterMahasiswa

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
        observeEnrolledClasses()
        observeDosenNames()

        binding.btnEnrollClassClassroom.setOnClickListener {
            val uri = "enroll_class://enroll_class_activity".toUri()
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        classroomViewModel.fetchEnrolledClasses()
    }

    private fun setupRecyclerView() {
        classroomAdapter = ClassroomAdapterMahasiswa()
        binding.rvClassroom.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = classroomAdapter
            setHasFixedSize(true)
        }

        classroomAdapter.getDosenName = { nidn ->
            val resolvedName = classroomViewModel.getResolvedDosenName(nidn)
            resolvedName
        }

        classroomAdapter.onItemClick = { kelas ->
            val uri = "detail_class://detail_class_registered_activity".toUri()
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.putExtra("kelasId", kelas.kelasId)
            startActivity(intent)
        }
    }

    private fun observeEnrolledClasses() {
        classroomViewModel.enrolledClasses.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarClassroom.visibility = View.VISIBLE
                    binding.rvClassroom.visibility = View.GONE
                    binding.ivDoesntHaveAnClassroom.visibility = View.GONE
                    binding.tvDoesntHaveAnClassroom.visibility = View.GONE
                }

                is Resource.Success -> {
                    binding.progressBarClassroom.visibility = View.GONE
                    val classes = resource.data
                    if (classes.isNullOrEmpty()) {
                        binding.rvClassroom.visibility = View.GONE
                        binding.ivDoesntHaveAnClassroom.visibility = View.VISIBLE
                        binding.tvDoesntHaveAnClassroom.visibility = View.VISIBLE
                        binding.tvTotalClassJoined.text = "0"
                    } else {
                        binding.rvClassroom.visibility = View.VISIBLE
                        binding.ivDoesntHaveAnClassroom.visibility = View.GONE
                        binding.tvDoesntHaveAnClassroom.visibility = View.GONE
                        classroomAdapter.submitList(classes)
                        binding.tvTotalClassJoined.text = classes.size.toString()
                    }
                }

                is Resource.Error -> {
                    binding.progressBarClassroom.visibility = View.GONE
                    binding.rvClassroom.visibility = View.GONE
                    binding.ivDoesntHaveAnClassroom.visibility = View.VISIBLE
                    binding.tvDoesntHaveAnClassroom.visibility = View.VISIBLE
                    binding.tvDoesntHaveAnClassroom.text =
                        resource.message ?: getString(R.string.failed_to_load_class)
                    Toast
                        .makeText(
                            requireContext(),
                            resource.message ?: getString(R.string.trouble_error),
                            Toast.LENGTH_LONG,
                        ).show()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeDosenNames() {
        classroomViewModel.dosenNamesMap.observe(viewLifecycleOwner) { map ->
            if (::classroomAdapter.isInitialized && binding.rvClassroom.adapter != null) {
                classroomAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvClassroom.adapter = null
        _binding = null
    }
}
