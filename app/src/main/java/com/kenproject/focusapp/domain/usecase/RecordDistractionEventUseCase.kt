package com.kenproject.focusapp.domain.usecase

import com.kenproject.focusapp.domain.model.DetectionThresholds
import com.kenproject.focusapp.domain.model.DistractionEvent
import com.kenproject.focusapp.domain.model.DistractionSignal
import com.kenproject.focusapp.domain.model.DistractionType
import com.kenproject.focusapp.domain.repository.SessionRepository
import javax.inject.Inject

class RecordDistractionEventUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val thresholds: DetectionThresholds = DetectionThresholds()
) {
    suspend operator fun invoke(
        signal: DistractionSignal,
        sessionId: String
    ): DistractionEvent? {
        val isDistraction = when (signal) {
            is DistractionSignal.Noise -> signal.value >= thresholds.noiseThreshold
            is DistractionSignal.Movement -> signal.value >= thresholds.movementThreshold
        }
        if (!isDistraction) return null

        val event = DistractionEvent(
            sessionId = sessionId,
            type = when (signal) {
                is DistractionSignal.Noise -> DistractionType.NOISE
                is DistractionSignal.Movement -> DistractionType.MOVEMENT
            },
            intensity = signal.value
        )
        sessionRepository.addDistractionEvent(event)
        return event
    }
}