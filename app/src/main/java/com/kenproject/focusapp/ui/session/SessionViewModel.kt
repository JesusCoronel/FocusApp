package com.kenproject.focusapp.ui.session

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kenproject.focusapp.R
import com.kenproject.focusapp.domain.model.DistractionEvent
import com.kenproject.focusapp.domain.model.DistractionSignal
import com.kenproject.focusapp.domain.model.FocusSession
import com.kenproject.focusapp.domain.repository.DistractionDetector
import com.kenproject.focusapp.domain.repository.SessionRepository
import com.kenproject.focusapp.domain.usecase.RecordDistractionEventUseCase
import com.kenproject.focusapp.domain.usecase.StartFocusSessionUseCase
import com.kenproject.focusapp.domain.usecase.StopFocusSessionUseCase
import com.kenproject.focusapp.util.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject
import javax.inject.Named

data class SessionUiState(
    val isSessionActive: Boolean = false,
    val sessionDurationSeconds: Long = 0L,
    val currentSessionEvents: List<DistractionEvent> = emptyList(),
    val allHistoricEvents: List<DistractionEvent> = emptyList(),
    val currentNoiseLevel: Float = 0f,
    val currentMovementLevel: Float = 0f,
    val isLoadingStart: Boolean = false,
    val isInitializing: Boolean = true,
    val errorResId: Int? = null,
    val paramError: String? = null
) {
    val currentDistractionCount: Int get() = currentSessionEvents.size
}

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val startSession: StartFocusSessionUseCase,
    private val stopSession: StopFocusSessionUseCase,
    private val recordDistraction: RecordDistractionEventUseCase,
    private val sessionRepository: SessionRepository,
    @Named("noise") private val noiseDetector: DistractionDetector,
    @Named("movement") private val movementDetector: DistractionDetector,
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    private var activeSession: FocusSession? = null
    private var timerJob: Job? = null
    private var detectorJob: Job? = null
    private var currentSessionEventsJob: Job? = null

    init {
        initialize()
    }

    private fun initialize() {
        viewModelScope.launch {

            sessionRepository.abortOrphanedSessions()

            sessionRepository.observeAllDistractionEvents()
                .onEach { allEvents ->
                    _uiState.update { it.copy(allHistoricEvents = allEvents) }
                }
                .launchIn(viewModelScope)

            _uiState.update { it.copy(isInitializing = false) }
        }
    }

    fun startFocusSession() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingStart = true, errorResId = null, paramError = null) }
            try {
                val session = startSession()
                activeSession = session
                _uiState.update {
                    it.copy(
                        isSessionActive = true,
                        isLoadingStart = false,
                        sessionDurationSeconds = 0L,
                        currentSessionEvents = emptyList()
                    )
                }
                observeCurrentSessionEvents(session.id)
                startTimer(session.startTime)
                startDetectors()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingStart = false,
                        errorResId = R.string.error_failed_to_start_session,
                        paramError = e.message
                    )
                }
            }
        }
    }

    fun stopFocusSession() {
        val session = activeSession ?: return
        viewModelScope.launch {
            stopDetectors()
            timerJob?.cancel()
            currentSessionEventsJob?.cancel()
            stopSession(session.id)
            activeSession = null
            _uiState.update {
                it.copy(
                    isSessionActive = false,
                    currentSessionEvents = emptyList(),
                    sessionDurationSeconds = 0L,
                    currentNoiseLevel = 0f,
                    currentMovementLevel = 0f
                )
            }
        }
    }

    private fun observeCurrentSessionEvents(sessionId: String) {
        currentSessionEventsJob?.cancel()
        currentSessionEventsJob = viewModelScope.launch {
            sessionRepository.observeDistractionEvents(sessionId)
                .collect { events ->
                    _uiState.update { it.copy(currentSessionEvents = events) }
                }
        }
    }

    private fun startTimer(startTime: Instant = Instant.now()) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                val elapsed = Instant.now().epochSecond - startTime.epochSecond
                _uiState.update { it.copy(sessionDurationSeconds = elapsed) }
                delay(1000L)
            }
        }
    }

    private fun startDetectors() {
        noiseDetector.start()
        movementDetector.start()

        detectorJob = viewModelScope.launch {
            merge(noiseDetector.signals, movementDetector.signals)
                .collect { signal ->
                    val sessionId = activeSession?.id ?: return@collect

                    _uiState.update { state ->
                        state.copy(
                            currentNoiseLevel = when (signal) {
                                is DistractionSignal.Noise -> signal.value
                                else -> state.currentNoiseLevel
                            },
                            currentMovementLevel = when (signal) {
                                is DistractionSignal.Movement -> signal.value
                                else -> state.currentMovementLevel
                            }
                        )
                    }

                    val event = recordDistraction(signal, sessionId)
                    if (event != null) {
                        notificationHelper.notifyDistraction(event.type)
                    }
                }
        }
    }

    private fun stopDetectors() {
        detectorJob?.cancel()
        detectorJob = null
        noiseDetector.stop()
        movementDetector.stop()
    }

    override fun onCleared() {
        super.onCleared()
        stopDetectors()
        timerJob?.cancel()
        currentSessionEventsJob?.cancel()
    }
}