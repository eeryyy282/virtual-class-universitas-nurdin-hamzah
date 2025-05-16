package com.mjs.virtualclassuniversitasnurdinhamzah.ui.classroom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mjs.virtual_class_universitas_nurdin_hamzah.R

class ClassroomFragment : Fragment() {

    companion object {
        fun newInstance() = ClassroomFragment()
    }

    private val viewModel: ClassroomViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_classroom, container, false)
    }
}
