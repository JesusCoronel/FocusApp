package com.kenproject.focusapp.data.local

import com.kenproject.focusapp.data.local.entities.DistractionEventEntity
import com.kenproject.focusapp.data.local.entities.SessionEntity
import com.kenproject.focusapp.domain.model.DistractionEvent
import com.kenproject.focusapp.domain.model.DistractionType
import com.kenproject.focusapp.domain.model.FocusSession
import com.kenproject.focusapp.domain.model.SessionStatus
import java.time.Instant

fun SessionEntity.toDomain(events: List<DistractionEvent> = emptyList()) = FocusSession(
    id = id,
    startTime = Instant.ofEpochSecond(startTime),
    endTime = endTime?.let { Instant.ofEpochSecond(it) },
    distractionEvents = events,
    status = SessionStatus.valueOf(status)
)

fun FocusSession.toEntity() = SessionEntity(
    id = id,
    startTime = startTime.epochSecond,
    endTime = endTime?.epochSecond,
    status = status.name
)

fun DistractionEventEntity.toDomain() = DistractionEvent(
    id = id,
    sessionId = sessionId,
    timestamp = Instant.ofEpochSecond(timestamp),
    type = DistractionType.valueOf(type),
    intensity = intensity
)

fun DistractionEvent.toEntity() = DistractionEventEntity(
    id = id,
    sessionId = sessionId,
    timestamp = timestamp.epochSecond,
    type = type.name,
    intensity = intensity
)