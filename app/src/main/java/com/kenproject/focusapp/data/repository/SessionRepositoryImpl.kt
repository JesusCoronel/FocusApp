package com.kenproject.focusapp.data.repository

import android.content.Context
import com.kenproject.focusapp.R
import com.kenproject.focusapp.data.local.dao.DistractionEventDao
import com.kenproject.focusapp.data.local.dao.SessionDao
import com.kenproject.focusapp.data.local.toDomain
import com.kenproject.focusapp.data.local.toEntity
import com.kenproject.focusapp.data.remote.api.FocusApi
import com.kenproject.focusapp.data.remote.dto.CreateSessionRequest
import com.kenproject.focusapp.data.remote.dto.DistractionEventDto
import com.kenproject.focusapp.domain.model.DistractionEvent
import com.kenproject.focusapp.domain.model.FocusSession
import com.kenproject.focusapp.domain.repository.SessionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val sessionDao: SessionDao,
    private val eventDao: DistractionEventDao,
    private val api: FocusApi,
    @ApplicationContext private val context: Context
) : SessionRepository {

    override fun observeActiveSessions(): Flow<List<FocusSession>> =
        sessionDao.observeActive().map { entities ->
            entities.map { entity ->
                val events = eventDao.getBySession(entity.id).map { it.toDomain() }
                entity.toDomain(events)
            }
        }

    override fun observeAllSessions(): Flow<List<FocusSession>> =
        sessionDao.observeAll().map { entities ->
            entities.map { entity ->
                val events = eventDao.getBySession(entity.id).map { it.toDomain() }
                entity.toDomain(events)
            }
        }

    override fun observeAllDistractionEvents(): Flow<List<DistractionEvent>> =
        eventDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun abortOrphanedSessions() {
        sessionDao.abortAllActiveSessions(endTime = System.currentTimeMillis() / 1000)
    }

    override fun observeDistractionEvents(sessionId: String): Flow<List<DistractionEvent>> =
        eventDao.observeBySession(sessionId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun getActiveSession(): FocusSession? =
        sessionDao.getFirstActive()?.let { entity ->
            val events = eventDao.getBySession(entity.id).map { it.toDomain() }
            entity.toDomain(events)
        }

    override suspend fun getSessionById(id: String): FocusSession? {
        val entity = sessionDao.getById(id) ?: return null
        val events = eventDao.getBySession(id).map { it.toDomain() }
        return entity.toDomain(events)
    }

    override suspend fun saveSession(session: FocusSession) {
        sessionDao.insert(session.toEntity())
    }

    override suspend fun updateSession(session: FocusSession) {
        sessionDao.update(session.toEntity())
    }

    override suspend fun addDistractionEvent(event: DistractionEvent) {
        eventDao.insert(event.toEntity())
    }

    override suspend fun syncSessionToRemote(sessionId: String): Result<Unit> {
        return try {
            val session = getSessionById(sessionId)
                ?: return Result.failure(IllegalStateException(context.getString(R.string.error_session_not_found)))

            val request = CreateSessionRequest(
                id = session.id,
                startTime = session.startTime.epochSecond,
                endTime = session.endTime?.epochSecond,
                status = session.status.name,
                distractionEvents = session.distractionEvents.map { event ->
                    DistractionEventDto(
                        id = event.id,
                        sessionId = event.sessionId,
                        timestamp = event.timestamp.epochSecond,
                        type = event.type.name,
                        intensity = event.intensity
                    )
                }
            )

            val response = api.createSession(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(context.getString(R.string.error_api_generic, response.code())))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}