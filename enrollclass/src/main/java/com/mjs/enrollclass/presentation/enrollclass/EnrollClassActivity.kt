package com.mjs.enrollclass.presentation.enrollclass

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.mjs.core.data.Resource
import com.mjs.core.ui.classroom.ClassroomAdapter
import com.mjs.enrollclass.R
import com.mjs.enrollclass.databinding.ActivityEnrollClassBinding
import com.mjs.enrollclass.di.enrollClassModule
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules

class EnrollClassActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEnrollClassBinding
    private val enrollClassViewModel: EnrollClassViewModel by viewModel()
    private lateinit var classroomAdapter: ClassroomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loadKoinModules(enrollClassModule)

        binding = ActivityEnrollClassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        checkDarkMode()
        setupRecyclerView()
        observeViewModelData()
        setupSearch()
    }

    private fun setupSearch() {
        binding.etSearchClass.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int,
                ) {
                    enrollClassViewModel.setSearchQuery(s.toString())
                }

                override fun afterTextChanged(s: Editable?) {
                }
            },
        )
    }

    private fun setupRecyclerView() {
        classroomAdapter = ClassroomAdapter()
        binding.rvClassroom.apply {
            layoutManager = LinearLayoutManager(this@EnrollClassActivity)
            adapter = classroomAdapter
            setHasFixedSize(true)
        }

        classroomAdapter.onItemClick = { kelas ->
            val uri = "detail_class://detail_class_unregistered_activity".toUri()
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.putExtra("kelasId", kelas.kelasId)
            startActivity(intent)
        }

        classroomAdapter.getDosenName = { nidn ->
            enrollClassViewModel.dosenMap.value?.get(nidn)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeViewModelData() {
        enrollClassViewModel.groupedClasses.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarEnrollClass.visibility = View.VISIBLE
                    binding.rvClassroom.visibility = View.GONE
                    binding.tvNoClassAvailable.visibility = View.GONE
                    binding.ivNoClassAvailable.visibility = View.GONE
                }

                is Resource.Success -> {
                    binding.progressBarEnrollClass.visibility = View.GONE
                    val groupedClasses = resource.data
                    if (groupedClasses.isNullOrEmpty()) {
                        binding.rvClassroom.visibility = View.GONE
                        binding.tvNoClassAvailable.visibility = View.VISIBLE
                        binding.ivNoClassAvailable.visibility = View.VISIBLE
                    } else {
                        binding.rvClassroom.visibility = View.VISIBLE
                        binding.tvNoClassAvailable.visibility = View.GONE
                        binding.ivNoClassAvailable.visibility = View.GONE
                        classroomAdapter.setData(groupedClasses)
                    }
                }

                is Resource.Error -> {
                    binding.progressBarEnrollClass.visibility = View.GONE
                    binding.rvClassroom.visibility = View.GONE
                    binding.tvNoClassAvailable.visibility = View.VISIBLE
                    binding.ivNoClassAvailable.visibility = View.VISIBLE
                    binding.tvNoClassAvailable.text =
                        resource.message ?: getString(R.string.failed_to_load_class)
                    Toast
                        .makeText(
                            this,
                            resource.message ?: getString(R.string.error_throuble),
                            Toast.LENGTH_LONG,
                        ).show()
                }
            }
        }

        enrollClassViewModel.dosenMap.observe(this) { dosenMap ->
            if (::classroomAdapter.isInitialized) {
                classroomAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun checkDarkMode() {
        enrollClassViewModel.getThemeSetting.observe(this) {
            if (it) {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
            } else {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.rvClassroom.adapter = null
    }
}
