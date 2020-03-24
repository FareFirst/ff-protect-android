package com.amahop.farefirst.ffprotect.ui.dashboard.customview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.content.res.AppCompatResources
import com.amahop.farefirst.ffprotect.R
import com.amahop.farefirst.ffprotect.utils.DateHelper
import com.amahop.farefirst.ffprotect.utils.Settings
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.view_tracker_info_card.view.*

class TrackerInfoCardView(context: Context, attrs: AttributeSet) :
    MaterialCardView(context, attrs) {

    private var isTrackerRunning = false
    private var lastSyncedAt = 0L
    private var phoneNumber: String? = null

    init {
        inflate(context, R.layout.view_tracker_info_card, this)
    }

    fun setIsTrackerRunning(isTrackerRunning: Boolean) {
        this.isTrackerRunning = isTrackerRunning

        var drawableRId = if (isTrackerRunning) {
            tvStatus.text = context.getString(R.string.tracker_running)
            R.drawable.ic_running_24dp
        } else {
            tvStatus.text = context.getString(R.string.tracker_not_running)
            R.drawable.ic_not_running_24dp
        }

        val drawable =
            AppCompatResources.getDrawable(context, drawableRId)
        tvStatus.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
    }


    fun setLastSyncedAt(lastSyncedAt: Long) {
        this.lastSyncedAt = lastSyncedAt

        if (lastSyncedAt == Settings.MIN_TIME) {
            tvLastSyncedAt.text = context.getString(R.string.never_synced)
        } else {
            tvLastSyncedAt.text = DateHelper.getRelativeDateTime(lastSyncedAt)
        }
    }

    fun setPhoneNumber(phoneNumber: String?) {
        this.phoneNumber = phoneNumber

        tvPhoneNumber.text = phoneNumber ?: context.getString(R.string.unknown)
    }
}





