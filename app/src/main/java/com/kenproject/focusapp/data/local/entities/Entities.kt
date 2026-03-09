package com.kenproject.focusapp.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "focus_sessions")
data class SessionEntity(
    @PrimaryKey val id: String,
    val startTime: Long,
    val endTime: Long?,
    val status: String
)

@Entity(
    tableName = "distraction_events",
    foreignKeys = [
        ForeignKey(
            entity = SessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId")]
)
data class DistractionEventEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val timestamp: Long,
    val type: String,
    val intensity: Float
)