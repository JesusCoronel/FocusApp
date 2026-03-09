package com.kenproject.focusapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kenproject.focusapp.data.local.dao.DistractionEventDao
import com.kenproject.focusapp.data.local.dao.SessionDao
import com.kenproject.focusapp.data.local.entities.DistractionEventEntity
import com.kenproject.focusapp.data.local.entities.SessionEntity

@Database(
    entities = [SessionEntity::class, DistractionEventEntity::class],
    version = 1,
    exportSchema = true
)
abstract class FocusDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun distractionEventDao(): DistractionEventDao
}