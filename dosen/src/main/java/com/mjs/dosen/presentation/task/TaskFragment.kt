package com.mjs.dosen.presentation.task

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
import com.mjs.core.ui.task.TaskAdapterDosenCategorized
import com.mjs.dosen.databinding.FragmentTaskBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class TaskFragment : Fragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by viewModel()
    private lateinit var taskAdapter: TaskAdapterDosenCategorized

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
        taskAdapter = TaskAdapterDosenCategorized()
        taskAdapter.getClassName = { kelasId ->
            viewModel.getClassNameById(kelasId)
        }
        taskAdapter.getClassPhotoProfile = { kelasId ->
            viewModel.getClassPhotoProfileById(kelasId)
        }
        binding.rvTaskDosen.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = taskAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeTasks() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tasks.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.progressBarTaskTask.visibility = View.VISIBLE
                            binding.rvTaskDosen.visibility = View.GONE
                            binding.tvDoesntHaveAnTask.visibility = View.GONE
                            binding.ivDoesntHaveAnTask.visibility = View.GONE
                        }

                        is Resource.Success -> {
                            binding.progressBarTaskTask.visibility = View.GONE
                            val (activeTasks, pastDeadlineTasks) =
                                resource.data
                                    ?: Pair(emptyList(), emptyList())
                            if (activeTasks.isEmpty() && pastDeadlineTasks.isEmpty()) {
                                binding.rvTaskDosen.visibility = View.GONE
                                binding.tvDoesntHaveAnTask.visibility = View.VISIBLE
                                binding.ivDoesntHaveAnTask.visibility = View.VISIBLE
                                binding.tvDoesntHaveAnTask.text =
                                    getString(com.mjs.core.R.string.doesnt_have_an_task_text)
                            } else {
                                taskAdapter.setData(activeTasks, pastDeadlineTasks)
                                binding.rvTaskDosen.visibility = View.VISIBLE
                                binding.tvDoesntHaveAnTask.visibility = View.GONE
                                binding.ivDoesntHaveAnTask.visibility = View.GONE
                            }
                        }

                        is Resource.Error -> {
                            binding.progressBarTaskTask.visibility = View.GONE
                            binding.rvTaskDosen.visibility = View.GONE
                            binding.tvDoesntHaveAnTask.text = resource.message
                                ?: getString(com.mjs.core.R.string.error_loading_tasks)
                            binding.tvDoesntHaveAnTask.visibility = View.VISIBLE
                            binding.ivDoesntHaveAnTask.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvTaskDosen.adapter = null
        _binding = null
    }
}
