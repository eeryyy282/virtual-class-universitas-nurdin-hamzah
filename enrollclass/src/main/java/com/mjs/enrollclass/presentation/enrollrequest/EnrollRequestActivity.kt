package com.mjs.enrollclass.presentation.enrollrequest

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Mahasiswa
import com.mjs.core.ui.MahasiswaEnrollRequestAdapter
import com.mjs.enrollclass.databinding.ActivityEnrollRequestBinding
import com.mjs.enrollclass.di.enrollClassModule
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules

class EnrollRequestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEnrollRequestBinding
    private val enrollRequestViewModel: EnrollRequestViewModel by viewModel()
    private var kelasId: String? = null
    private lateinit var mahasiswaEnrollRequestAdapter: MahasiswaEnrollRequestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        loadKoinModules(enrollClassModule)
        binding = ActivityEnrollRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()

        kelasId = intent.data?.getQueryParameter("kelasId")

        if (kelasId != null) {
            observeEnrollmentRequests()
            enrollRequestViewModel.fetchEnrollmentRequests(kelasId!!)
        } else {
            Toast.makeText(this, "Error: Kelas ID tidak ditemukan", Toast.LENGTH_LONG).show()
            binding.progressBarEnrollRequest.visibility = View.GONE
            finish()
        }

        checkDarkMode()
    }

    private fun setupRecyclerView() {
        mahasiswaEnrollRequestAdapter =
            MahasiswaEnrollRequestAdapter(
                onAcceptClickListener = { mahasiswa ->
                    handleAcceptRequest(mahasiswa)
                },
                onRejectClickListener = { mahasiswa ->
                    handleRejectRequest(mahasiswa)
                },
            )
        binding.rvListMahasiswaRequestEnroll.apply {
            layoutManager = LinearLayoutManager(this@EnrollRequestActivity)
            adapter = mahasiswaEnrollRequestAdapter
        }
    }

    private fun observeEnrollmentRequests() {
        enrollRequestViewModel.enrollmentRequests.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarEnrollRequest.visibility = View.VISIBLE
                    binding.rvListMahasiswaRequestEnroll.visibility = View.GONE
                    binding.ivNoMahasiswaRequest.visibility = View.GONE
                    binding.tvNoClassAvailable.visibility = View.GONE
                }

                is Resource.Success -> {
                    binding.progressBarEnrollRequest.visibility = View.GONE
                    val mahasiswaList = resource.data
                    if (mahasiswaList != null && mahasiswaList.isNotEmpty()) {
                        mahasiswaEnrollRequestAdapter.submitList(mahasiswaList)
                        binding.rvListMahasiswaRequestEnroll.visibility = View.VISIBLE
                        binding.ivNoMahasiswaRequest.visibility = View.GONE
                        binding.tvNoClassAvailable.visibility = View.GONE
                    } else {
                        binding.rvListMahasiswaRequestEnroll.visibility = View.GONE
                        binding.ivNoMahasiswaRequest.visibility = View.VISIBLE
                        binding.tvNoClassAvailable.visibility = View.VISIBLE
                    }
                }

                is Resource.Error -> {
                    binding.progressBarEnrollRequest.visibility = View.GONE
                    binding.rvListMahasiswaRequestEnroll.visibility = View.GONE
                    binding.ivNoMahasiswaRequest.visibility = View.VISIBLE
                    binding.tvNoClassAvailable.visibility = View.VISIBLE
                    Toast
                        .makeText(
                            this,
                            resource.message ?: "Gagal memuat daftar permintaan",
                            Toast.LENGTH_LONG,
                        ).show()
                }
            }
        }
    }

    private fun handleAcceptRequest(mahasiswa: Mahasiswa) {
        Toast.makeText(this, "Accept: ${mahasiswa.nama}", Toast.LENGTH_SHORT).show()
    }

    private fun handleRejectRequest(mahasiswa: Mahasiswa) {
        Toast.makeText(this, "Reject: ${mahasiswa.nama}", Toast.LENGTH_SHORT).show()
    }

    private fun checkDarkMode() {
        enrollRequestViewModel.getThemeSetting.observe(this) {
            if (it) {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
            } else {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            }
        }
    }
}
