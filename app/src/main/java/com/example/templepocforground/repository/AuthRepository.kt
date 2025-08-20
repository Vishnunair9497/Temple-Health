package com.example.templepocforground.repository

import com.example.templepocforground.data.ApiService
import com.example.templepocforground.models.AcknowledgeRequest
import com.example.templepocforground.models.AuthRequest
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: ApiService,
) {
    suspend fun login(email: String, password: String) = api.login(AuthRequest(email, password))
    suspend fun getSocketUrl(uid: String) = api.getSocketUrl(uid)
    suspend fun stopAlertsApi(
        notificationId: String,
        providerId: String
    ) = api.acknowledgeNotification(
        AcknowledgeRequest(
            NotificationId = notificationId,
            ProviderId = providerId
        )
    )

}