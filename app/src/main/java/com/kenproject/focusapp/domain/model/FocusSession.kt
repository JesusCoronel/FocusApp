package com.kenproject.focusapp.domain.model

import java.time.Instant
import java.util.UUID

data class FocusSession(
    val id: String = UUID.randomUUID().toString(),
    val startTime: Instant,
    val endTime: Instant? = null,
    val distractionEvents: List<DistractionEvent> = emptyList(),
    val status: SessionStatus = SessionStatus.ACTIVE
)

enum class SessionStatus {
    ACTIVE,
    COMPLETED,
    ABORTED
}