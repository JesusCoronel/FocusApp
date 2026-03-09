package com.kenproject.focusapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DistractionEventDto(
    @SerializedName("id") val id: String,
    @SerializedName("session_id") val sessionId: String,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("type") val type: String,
    @SerializedName("intensity") val intensity: Float
)