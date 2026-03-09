package com.kenproject.focusapp.domain.model

import com.kenproject.focusapp.R
import java.time.Instant
import java.util.UUID

data class DistractionEvent(
    val id: String = UUID.randomUUID().toString(),
    val sessionId: String,
    val timestamp: Instant = Instant.now(),
    val type: DistractionType,
    val intensity: Float
)

enum class DistractionType(val displayResId: Int) {
    NOISE(R.string.distraction_type_noise),
    MOVEMENT(R.string.distraction_type_movement)
}