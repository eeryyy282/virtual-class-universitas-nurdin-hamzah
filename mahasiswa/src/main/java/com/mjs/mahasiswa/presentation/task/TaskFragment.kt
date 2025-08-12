package com.mjs.mahasiswa.presentation.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.mjs.core.data.Resource
import com.mjs.core.ui.task.TaskAdapterMahasiswa
import com.mjs.mahasiswa.databinding.FragmentTaskBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class TaskFragment : Fragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by viewModel()
    private lateinit var taskAdapter: TaskAdapterMahasiswa

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeTasks()
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapterMahasiswa()
        taskAdapter.getClassName = {
            viewModel.getClassNameById(it)
        }
        taskAdapter.getClassPhotoProfile = {
            viewModel.getClassPhotoProfileById(it)
        }
        binding.rvTaskTask.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = taskAdapter
        }
    }

    private fun observeTasks() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tasks.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.progressBarTaskTask.visibility = View.VISIBLE
                            binding.rvTaskTask.visibility = View.GONE
                            binding.tvDoesntHaveAnTask.visibility = View.GONE
                            binding.ivDoesntHaveAnTask.visibility = View.GONE
                        }

                        is Resource.Success -> {
                            binding.progressBarTaskTask.visibility = View.GONE
                            val (notFinishedTasks, lateTasks) =
                                resource.data ?: Pair(
                                    emptyList(),
                                    emptyList(),
                                )
                            if (notFinishedTasks.isEmpty() && lateTasks.isEmpty()) {
                                binding.rvTaskTask.visibility = View.GONE
                                binding.tvDoesntHaveAnTask.visibility = View.VISIBLE
                                binding.ivDoesntHaveAnTask.visibility = View.VISIBLE
                            } else {
                                taskAdapter.setData(notFinishedTasks, lateTasks)
                                binding.rvTaskTask.visibility = View.VISIBLE
                                binding.tvDoesntHaveAnTask.visibility = View.GONE
                                binding.ivDoesntHaveAnTask.visibility = View.GONE
                            }
                        }

                        is Resource.Error -> {
                            binding.progressBarTaskTask.visibility = View.GONE
                            binding.rvTaskTask.visibility = View.GONE
                            binding.tvDoesntHaveAnTask.text =
                                resource.message ?: "Error memuat tugas"
                            binding.tvDoesntHaveAnTask.visibility = View.VISIBLE
                            binding.ivDoesntHaveAnTask.visibility =
                                View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvTaskTask.adapter = null
        _binding = null
    }
}
