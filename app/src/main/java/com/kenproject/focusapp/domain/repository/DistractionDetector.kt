package com.kenproject.focusapp.domain.repository

import com.kenproject.focusapp.domain.model.DistractionSignal
import kotlinx.coroutines.flow.Flow

interface DistractionDetector {
    val signals: Flow<DistractionSignal>
    fun start()
    fun stop()
}