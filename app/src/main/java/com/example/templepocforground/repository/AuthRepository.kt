package com.example.templepocforground.repository

import android.util.Log
import com.example.templepocforground.data.ApiService
import com.example.templepocforground.models.AcknowledgeRequest
import com.example.templepocforground.models.AuthRequest
import com.example.templepocforground.models.DeviceRegisterRequest
import com.example.templepocforground.models.DeviceRegisterResponse
import com.example.templepocforground.models.NegotiateRequest
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: ApiService,
) {
    suspend fun login(email: String, password: String) = api.login(AuthRequest(email, password))
    suspend fun getSocketUrl(uid: String) = api.getSocketUrl(NegotiateRequest(uid, "V:1", "mobile"))
    suspend fun stopAlertsApi(
        notificationId: String, providerId: String
    ) = api.acknowledgeNotification(
        AcknowledgeRequest(
            AlertId = notificationId, UserId = providerId
        )
    )
    fun registerDevice(request: DeviceRegisterRequest): Flow<Result<DeviceRegisterResponse>> = flow {
        try {
            val response =  api.deviceRegister(request)
            emit(Result.success(response))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}