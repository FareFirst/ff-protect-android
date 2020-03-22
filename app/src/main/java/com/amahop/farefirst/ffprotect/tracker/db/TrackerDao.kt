package com.amahop.farefirst.ffprotect.tracker.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TrackerDao {
    @Insert
    suspend fun insertAll(trackers: ArrayList<Tracker>)

    @Query("SELECT * FROM trackers WHERE createdAt < :syncedAt LIMIT 5000")
    suspend fun trackersSync(syncedAt: Long): Array<Tracker>

    // IN query is a hack to avoid limitation of LIMIT in SQLLite for DELETE
    @Query("DELETE FROM trackers WHERE id IN (SELECT id FROM trackers WHERE createdAt < :syncedAt LIMIT 5000)")
    suspend fun deleteSynced(syncedAt: Long)
}