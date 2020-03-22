package com.amahop.farefirst.ffprotect.db

import android.content.Context
import androidx.room.Room

object DBProvider {
    private var db: AppDatabase? = null

    @Synchronized
    fun getDB(context: Context): AppDatabase {
        this.db = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "ff-protect"
        ).build()

        db?.let { return it } ?: kotlin.run {
            throw RuntimeException("Failed to get db instance")
        }
    }
}