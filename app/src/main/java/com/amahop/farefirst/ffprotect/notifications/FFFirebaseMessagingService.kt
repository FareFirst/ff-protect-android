package com.amahop.farefirst.ffprotect.notifications

import com.amahop.farefirst.ffprotect.utils.LogManager
import com.amahop.farefirst.ffprotect.utils.Settings
import com.google.firebase.messaging.FirebaseMessagingService

class FFFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        const val TAG = "FFFMessagingService"
    }

    override fun onNewToken(refreshedToken: String) {
        super.onNewToken(refreshedToken)
        LogManager.d(TAG, "Refreshed token: $refreshedToken");
        Settings.setFCMToken(refreshedToken)
    }
}