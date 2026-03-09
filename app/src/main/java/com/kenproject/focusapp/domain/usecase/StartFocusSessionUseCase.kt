package com.kenproject.focusapp.domain.usecase

import com.kenproject.focusapp.domain.model.FocusSession
import com.kenproject.focusapp.domain.model.SessionStatus
import com.kenproject.focusapp.domain.repository.SessionRepository
import java.time.Instant
import javax.inject.Inject

class StartFocusSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(): FocusSession {
        val session = FocusSession(
            startTime = Instant.now(),
            status = SessionStatus.ACTIVE
        )
        sessionRepository.saveSession(session)
        return session
    }
}