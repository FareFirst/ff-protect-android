package com.amahop.farefirst.ffprotect.notifications

import android.util.Log
import com.amahop.farefirst.ffprotect.utils.Settings
import com.google.firebase.messaging.FirebaseMessagingService

class FFFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        const val TAG = "FFFMessagingService"
    }

    override fun onNewToken(refreshedToken: String) {
        super.onNewToken(refreshedToken)
        Log.d(TAG, "Refreshed token: $refreshedToken");
        Settings.setFCMToken(refreshedToken)
    }
}