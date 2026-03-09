package com.kenproject.focusapp.domain.usecase

import com.kenproject.focusapp.domain.model.SessionStatus
import com.kenproject.focusapp.domain.repository.SessionRepository
import java.time.Instant
import javax.inject.Inject

class StopFocusSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(sessionId: String) {
        val session = sessionRepository.getSessionById(sessionId) ?: return
        val completedSession = session.copy(
            endTime = Instant.now(),
            status = SessionStatus.COMPLETED
        )
        sessionRepository.updateSession(completedSession)
        sessionRepository.syncSessionToRemote(sessionId)
    }
}