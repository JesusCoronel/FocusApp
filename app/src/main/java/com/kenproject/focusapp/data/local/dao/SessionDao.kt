package com.kenproject.focusapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kenproject.focusapp.data.local.entities.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM focus_sessions ORDER BY startTime DESC")
    fun observeAll(): Flow<List<SessionEntity>>

    @Query("SELECT * FROM focus_sessions WHERE status = 'ACTIVE'")
    fun observeActive(): Flow<List<SessionEntity>>

    @Query("SELECT * FROM focus_sessions WHERE status = 'ACTIVE' LIMIT 1")
    suspend fun getFirstActive(): SessionEntity?

    @Query("SELECT * FROM focus_sessions WHERE id = :id")
    suspend fun getById(id: String): SessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: SessionEntity)

    @Update
    suspend fun update(session: SessionEntity)

    @Query("UPDATE focus_sessions SET status = 'ABORTED', endTime = :endTime WHERE status = 'ACTIVE'")
    suspend fun abortAllActiveSessions(endTime: Long)
}