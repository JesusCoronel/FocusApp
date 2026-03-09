package com.kenproject.focusapp.data.remote.api

import com.kenproject.focusapp.data.remote.dto.CreateSessionRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface FocusApi {
    @POST("sessions")
    suspend fun createSession(@Body request: CreateSessionRequest): Response<Unit>
}