package com.mjs.virtualclassuniversitasnurdinhamzah.ui.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mjs.virtual_class_universitas_nurdin_hamzah.databinding.FragmentTaskBinding

class TaskFragment : Fragment() {

    private var _binding: FragmentTaskBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val taskViewModel =
            ViewModelProvider(this)[TaskViewModel::class.java]

        _binding = FragmentTaskBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textTask
        taskViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
