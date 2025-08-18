package com.mjs.enrollclass.presentation.enrollrequest

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
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

        createNotificationChannel()
        setupRecyclerView()

        kelasId = intent.data?.getQueryParameter("kelasId")

        if (kelasId != null) {
            observeEnrollmentRequests()
            observeEnrollmentUpdateStatus()
            enrollRequestViewModel.fetchEnrollmentRequests(kelasId!!)
        } else {
            Toast.makeText(this, "Error: Kelas ID tidak ditemukan", Toast.LENGTH_LONG).show()
            binding.progressBarEnrollRequest.visibility = View.GONE
            finish()
        }

        checkDarkMode()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Permintaan Pendaftaran"
            val descriptionText = "Notifikasi untuk permintaan pendaftaran mahasiswa baru"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(
                    EnrollRequestViewModel.ENROLL_REQUEST_CHANNEL_ID,
                    name,
                    importance,
                ).apply {
                    description = descriptionText
                }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
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
                        mahasiswaEnrollRequestAdapter.submitList(emptyList())
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

    private fun observeEnrollmentUpdateStatus() {
        enrollRequestViewModel.enrollmentUpdateStatus.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                }

                is Resource.Success -> {
                    Toast.makeText(this, resource.data, Toast.LENGTH_SHORT).show()
                    if (kelasId != null) {
                        enrollRequestViewModel.fetchEnrollmentRequests(kelasId!!)
                    }
                }

                is Resource.Error -> {
                    Toast
                        .makeText(
                            this,
                            resource.message ?: "Gagal memperbarui status",
                            Toast.LENGTH_SHORT,
                        ).show()
                }
            }
        }
    }

    private fun handleAcceptRequest(mahasiswa: Mahasiswa) {
        if (kelasId != null) {
            enrollRequestViewModel.acceptEnrollmentRequest(mahasiswa.nim, kelasId!!)
        } else {
            Toast.makeText(this, "Error: Kelas ID tidak valid", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleRejectRequest(mahasiswa: Mahasiswa) {
        if (kelasId != null) {
            enrollRequestViewModel.rejectEnrollmentRequest(mahasiswa.nim, kelasId!!)
        } else {
            Toast.makeText(this, "Error: Kelas ID tidak valid", Toast.LENGTH_SHORT).show()
        }
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
