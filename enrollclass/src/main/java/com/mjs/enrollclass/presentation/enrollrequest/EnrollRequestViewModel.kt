package com.mjs.enrollclass.presentation.enrollrequest

import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mjs.core.data.Resource
import com.mjs.core.domain.model.Mahasiswa
import com.mjs.core.domain.usecase.virtualclass.VirtualClassUseCase
import com.mjs.enrollclass.R
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EnrollRequestViewModel(
    private val application: Application,
    private val virtualClassUseCase: VirtualClassUseCase,
) : ViewModel() {
    val getThemeSetting = virtualClassUseCase.getThemeSetting().asLiveData()

    private val _enrollmentRequests = MutableLiveData<Resource<List<Mahasiswa>>>()
    val enrollmentRequests: LiveData<Resource<List<Mahasiswa>>> = _enrollmentRequests

    private val _enrollmentUpdateStatus = MutableLiveData<Resource<String>>()
    val enrollmentUpdateStatus: LiveData<Resource<String>> = _enrollmentUpdateStatus

    init {
        fetchTotalPendingRequestsAndNotifyDosen()
    }

    private fun fetchTotalPendingRequestsAndNotifyDosen() {
        @Suppress("ktlint:standard:no-consecutive-comments")
        viewModelScope.launch {
//            showEnrollmentNotification(1)
            try {
                val userType = virtualClassUseCase.getLoggedInUserType().first()
                if (userType == VirtualClassUseCase.USER_TYPE_DOSEN) {
                    val nidn = virtualClassUseCase.getLoggedInUserId().first()
                    if (nidn != null) {
                        val lastNotifiedCount =
                            virtualClassUseCase.getLastNotifiedPendingCount().first()

                        virtualClassUseCase
                            .getPendingEnrollmentRequestCountForDosen(nidn)
                            .collect { resourceResult ->
                                if (resourceResult is Resource.Success) {
                                    val currentCount = resourceResult.data
                                    if (currentCount != null && currentCount > 0 && currentCount > lastNotifiedCount) {
                                        showEnrollmentNotification(currentCount)
                                        viewModelScope.launch {
                                            virtualClassUseCase.saveLastNotifiedPendingCount(
                                                currentCount,
                                            )
                                        }
                                    }
                                }
                            }
                    }
                }
            } catch (_: Exception) {
            }
        }
    }

    private fun showEnrollmentNotification(pendingCount: Int) {
        val notificationManager =
            application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = application.packageManager.getLaunchIntentForPackage(application.packageName)
        val pendingIntent =
            PendingIntent.getActivity(
                application,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        val notificationBuilder =
            NotificationCompat
                .Builder(application, ENROLL_REQUEST_CHANNEL_ID)
                .setSmallIcon(R.drawable.invitation_icon)
                .setContentTitle("Permintaan Pendaftaran Baru")
                .setContentText("Ada $pendingCount permintaan pendaftaran mahasiswa baru di kelas Anda.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        notificationManager.notify(ENROLL_REQUEST_NOTIFICATION_ID, notificationBuilder.build())
    }

    fun fetchEnrollmentRequests(kelasId: String) {
        viewModelScope.launch {
            _enrollmentRequests.value = Resource.Loading()
            virtualClassUseCase.getPendingEnrollmentRequests(kelasId).collect {
                _enrollmentRequests.value = it
            }
        }
    }

    fun acceptEnrollmentRequest(
        nim: Int,
        kelasId: String,
    ) {
        viewModelScope.launch {
            _enrollmentUpdateStatus.value = Resource.Loading()
            virtualClassUseCase.acceptEnrollment(nim, kelasId).collect {
                _enrollmentUpdateStatus.value = it
                if (it is Resource.Success) {
                    fetchTotalPendingRequestsAndNotifyDosen()
                }
            }
        }
    }

    fun rejectEnrollmentRequest(
        nim: Int,
        kelasId: String,
    ) {
        viewModelScope.launch {
            _enrollmentUpdateStatus.value = Resource.Loading()
            virtualClassUseCase.rejectEnrollment(nim, kelasId).collect {
                _enrollmentUpdateStatus.value = it
                if (it is Resource.Success) {
                    fetchTotalPendingRequestsAndNotifyDosen()
                }
            }
        }
    }

    companion object {
        const val ENROLL_REQUEST_CHANNEL_ID = "enroll_request_channel"
        const val ENROLL_REQUEST_NOTIFICATION_ID = 1001
    }
}
