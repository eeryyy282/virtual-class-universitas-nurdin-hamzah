package com.mjs.enrollcourse.presentation.enrollcourse

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
import com.mjs.core.ui.course.CourseAdapter
import com.mjs.enrollcourse.R
import com.mjs.enrollcourse.databinding.ActivityEnrollCourseBinding
import com.mjs.enrollcourse.di.enrollCourseModule
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules

class EnrollCourseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEnrollCourseBinding
    private val enrollCourseViewModel: EnrollCourseViewModel by viewModel()
    private lateinit var courseAdapter: CourseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loadKoinModules(enrollCourseModule)
        binding = ActivityEnrollCourseBinding.inflate(layoutInflater)
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
        binding.etSearchCourse.addTextChangedListener(
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
                    enrollCourseViewModel.setSearchQuery(s.toString())
                }

                override fun afterTextChanged(s: Editable?) {
                }
            },
        )
    }

    private fun setupRecyclerView() {
        courseAdapter = CourseAdapter()
        binding.rvCourse.apply {
            layoutManager = LinearLayoutManager(this@EnrollCourseActivity)
            adapter = courseAdapter
            setHasFixedSize(true)
        }

        courseAdapter.onItemClick = { course ->
            val uri =
                "detail_course://detail_course_unregistered_activity".toUri()
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.putExtra("courseId", course.courseId)
            startActivity(intent)
        }

        courseAdapter.getLecturerName = { lecturerId ->
            enrollCourseViewModel.lecturerMap.value?.get(lecturerId)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeViewModelData() {
        enrollCourseViewModel.groupedCourses.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarEnrollCourse.visibility =
                        View.VISIBLE
                    binding.rvCourse.visibility = View.GONE
                    binding.tvNoCourseAvailable.visibility =
                        View.GONE
                    binding.ivNoCourseAvailable.visibility =
                        View.GONE
                }

                is Resource.Success -> {
                    binding.progressBarEnrollCourse.visibility =
                        View.GONE
                    val groupedCoursesData = resource.data
                    if (groupedCoursesData.isNullOrEmpty()) {
                        binding.rvCourse.visibility = View.GONE
                        binding.tvNoCourseAvailable.visibility =
                            View.VISIBLE
                        binding.ivNoCourseAvailable.visibility =
                            View.VISIBLE
                    } else {
                        binding.rvCourse.visibility = View.VISIBLE
                        binding.tvNoCourseAvailable.visibility =
                            View.GONE
                        binding.ivNoCourseAvailable.visibility =
                            View.GONE
                        courseAdapter.setData(groupedCoursesData)
                    }
                }

                is Resource.Error -> {
                    binding.progressBarEnrollCourse.visibility =
                        View.GONE
                    binding.rvCourse.visibility = View.GONE
                    binding.tvNoCourseAvailable.visibility =
                        View.VISIBLE
                    binding.ivNoCourseAvailable.visibility =
                        View.VISIBLE
                    binding.tvNoCourseAvailable.text =
                        resource.message
                            ?: getString(R.string.failed_to_load_course_data)
                    Toast
                        .makeText(
                            this,
                            resource.message ?: getString(R.string.error_generic),
                            Toast.LENGTH_LONG,
                        ).show()
                }
            }
        }

        enrollCourseViewModel.lecturerMap.observe(this) { lecturerMapData ->
            if (::courseAdapter.isInitialized) {
                if (binding.rvCourse.adapter != null) {
                    courseAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun checkDarkMode() {
        enrollCourseViewModel.getThemeSetting.observe(this) {
            if (it) {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
            } else {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.rvCourse.adapter = null
    }
}
