package com.kenproject.focusapp.domain.model

sealed class DistractionSignal {
    abstract val value: Float

    data class Noise(override val value: Float) : DistractionSignal()
    data class Movement(override val value: Float) : DistractionSignal()
}

data class DetectionThresholds(
    val noiseThreshold: Float = 0.1f,
    val movementThreshold: Float = 0.02f,
    val noisePollIntervalMs: Long = 1_500L,
    val movementDebounceMs: Long = 1_500L
)