package com.kenproject.focusapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kenproject.focusapp.data.local.entities.DistractionEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DistractionEventDao {
    @Query("SELECT * FROM distraction_events WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    suspend fun getBySession(sessionId: String): List<DistractionEventEntity>

    @Query("SELECT * FROM distraction_events WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun observeBySession(sessionId: String): Flow<List<DistractionEventEntity>>

    @Query("SELECT * FROM distraction_events ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<DistractionEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: DistractionEventEntity)
}