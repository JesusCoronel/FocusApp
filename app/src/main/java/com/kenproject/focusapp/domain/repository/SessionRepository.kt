package com.kenproject.focusapp.domain.repository

import com.kenproject.focusapp.domain.model.DistractionEvent
import com.kenproject.focusapp.domain.model.FocusSession
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun observeActiveSessions(): Flow<List<FocusSession>>
    fun observeAllSessions(): Flow<List<FocusSession>>
    fun observeDistractionEvents(sessionId: String): Flow<List<DistractionEvent>>
    fun observeAllDistractionEvents(): Flow<List<DistractionEvent>>
    suspend fun abortOrphanedSessions()
    suspend fun getActiveSession(): FocusSession?
    suspend fun getSessionById(id: String): FocusSession?
    suspend fun saveSession(session: FocusSession)
    suspend fun updateSession(session: FocusSession)
    suspend fun addDistractionEvent(event: DistractionEvent)
    suspend fun syncSessionToRemote(sessionId: String): Result<Unit>
}