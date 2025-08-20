package com.example.templepocforground.data

import com.example.templepocforground.models.AcknowledgeRequest
import com.example.templepocforground.models.AcknowledgeResponse
import com.example.templepocforground.models.AuthRequest
import com.example.templepocforground.models.AuthResponse
import com.example.templepocforground.models.NegotiateModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    @GET("negotiate")
    suspend fun getSocketUrl(
        @Query("userId") userId: String
    ): Response<NegotiateModel>

    @POST("notifications/acknowledge")
    suspend fun acknowledgeNotification(
        @Body request: AcknowledgeRequest
    ): Response<AcknowledgeResponse>
}