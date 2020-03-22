package com.amahop.farefirst.ffprotect.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.amahop.farefirst.ffprotect.tracker.db.Tracker
import com.amahop.farefirst.ffprotect.tracker.db.TrackerDao

@Database(entities = [Tracker::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackerDao(): TrackerDao
}