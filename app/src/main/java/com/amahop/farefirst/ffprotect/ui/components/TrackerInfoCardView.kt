package com.amahop.farefirst.ffprotect.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.amahop.farefirst.ffprotect.R
import com.amahop.farefirst.ffprotect.ui.dashboard.repositories.pojos.GovAlert
import com.amahop.farefirst.ffprotect.ui.dashboard.repositories.pojos.GovMessage
import com.amahop.farefirst.ffprotect.utils.DateHelper
import com.amahop.farefirst.ffprotect.utils.Settings
import com.amahop.farefirst.ffprotect.utils.ViewHelper
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.view_tracker_info_card.view.*

class TrackerInfoCardView(context: Context, attrs: AttributeSet) :
    MaterialCardView(context, attrs), View.OnClickListener {

    private var onRetryClickListener: (() -> Unit)? = null

    private var isTrackerRunning = false
    private var lastSyncedAt = 0L
    private var phoneNumber: String? = null
    private var isError: Boolean = false
    private var isLoading: Boolean = false
    private var message: GovMessage? = null

    init {
        inflate(context, R.layout.view_tracker_info_card, this)
        btnRetry.setOnClickListener(this)
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

    fun setIsError(value: Boolean) {
        isError = value
        handleStateDataUpdate()
    }

    fun setIsLoading(value: Boolean) {
        isLoading = value
        handleStateDataUpdate()
    }

    fun setMessage(value: GovMessage?) {
        message = value

        if (value != null) {
            ViewHelper.setHtmlTextToTextView(tvMessage, value.message)

            val colorRId = when (value.alertLevel) {
                GovAlert.RED -> R.color.alert_red
                GovAlert.YELLOW -> R.color.alert_yellow
                GovAlert.BLUE -> R.color.alert_blue
                GovAlert.GREEN -> R.color.alert_green
                GovAlert.DEFAULT -> android.R.color.black
            }
            tvMessage.setTextColor(ContextCompat.getColor(context, colorRId))
        }

        handleStateDataUpdate()
    }

    fun setOnRetryClickListener(listener: () -> Unit) {
        onRetryClickListener = listener
    }

    private fun handleStateDataUpdate() {
        when {
            isLoading -> {
                pbLoading.visibility = View.VISIBLE
                llError.visibility = View.GONE
                tvMessage.visibility = View.GONE
            }
            isError -> {
                pbLoading.visibility = View.GONE
                llError.visibility = View.VISIBLE
                tvMessage.visibility = View.GONE
            }
            message != null -> {
                pbLoading.visibility = View.GONE
                llError.visibility = View.GONE
                tvMessage.visibility = View.VISIBLE
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnRetry -> onRetryClick()
        }
    }

    private fun onRetryClick() {
        onRetryClickListener?.invoke()
    }
}





