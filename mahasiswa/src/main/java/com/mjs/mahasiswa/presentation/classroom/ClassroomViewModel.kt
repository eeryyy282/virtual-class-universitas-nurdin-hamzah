package com.mjs.mahasiswa.presentation.classroom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ClassroomViewModel : ViewModel() {
    private val _text =
        MutableLiveData<String>().apply {
            value = "This is classroom Fragment"
        }
    val text: LiveData<String> = _text
}
