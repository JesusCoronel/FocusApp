package com.kenproject.focusapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateSessionRequest(
    @SerializedName("id") val id: String,
    @SerializedName("start_time") val startTime: Long,
    @SerializedName("end_time") val endTime: Long?,
    @SerializedName("status") val status: String,
    @SerializedName("distraction_events") val distractionEvents: List<DistractionEventDto>
)