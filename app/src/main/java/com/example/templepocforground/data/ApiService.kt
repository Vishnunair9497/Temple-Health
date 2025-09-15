package com.example.templepocforground.data

import com.example.templepocforground.models.AcknowledgeRequest
import com.example.templepocforground.models.AcknowledgeResponse
import com.example.templepocforground.models.AuthRequest
import com.example.templepocforground.models.AuthResponse
import com.example.templepocforground.models.DeviceRegisterRequest
import com.example.templepocforground.models.DeviceRegisterResponse
import com.example.templepocforground.models.NegotiateModel
import com.example.templepocforground.models.NegotiateRequest
import com.example.templepocforground.models.OnCallStatusRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    @POST("negotiate")
    suspend fun getSocketUrl(@Body request: NegotiateRequest): Response<NegotiateModel>

    //   @POST("notifications/acknowledge")
    @POST("acks")
    suspend fun acknowledgeNotification(
        @Body request: AcknowledgeRequest
    ): Response<AcknowledgeResponse>

    @POST("devices/registration")
    suspend fun deviceRegister(@Body request: DeviceRegisterRequest) : DeviceRegisterResponse

    @POST("users/oncall")
    suspend fun getOnCallStatus(@Body request: OnCallStatusRequest) : String

}