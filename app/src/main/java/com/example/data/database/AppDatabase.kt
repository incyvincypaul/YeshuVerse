package com.example.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UserProgress::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProgressDao(): UserProgressDao
}
