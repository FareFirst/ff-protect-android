package com.amahop.farefirst.ffprotect.tracker.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "trackers", indices = [Index(value = ["createdAt"])])
data class Tracker(
    val trackerUuid: String,
    val bluetoothAddress: String?,
    val bluetoothName: String?,
    val distance: Double?,
    val rssi: Int?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var createdAt: Long = System.currentTimeMillis()
}