package com.amahop.farefirst.ffprotect.sync.network.pojo

import com.amahop.farefirst.ffprotect.tracker.db.Tracker

data class SyncData(
    val clientUserUid: String, // This value is used only for reference
    val location: Location?,
    val device: Device,
    val trackers: Array<Tracker>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SyncData

        if (clientUserUid != other.clientUserUid) return false
        if (location != other.location) return false
        if (device != other.device) return false
        if (!trackers.contentEquals(other.trackers)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = clientUserUid.hashCode()
        result = 31 * result + (location?.hashCode() ?: 0)
        result = 31 * result + device.hashCode()
        result = 31 * result + trackers.contentHashCode()
        return result
    }
}