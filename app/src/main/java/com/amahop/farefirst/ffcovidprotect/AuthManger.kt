package com.amahop.farefirst.ffcovidprotect

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth

object AuthManger {
    const val RC_SIGN_IN = 1001 // Should be globally unique
    const val TAG = "AuthManger"

    fun isSignedIn(): Boolean {
        val auth = FirebaseAuth.getInstance()
        return auth.currentUser != null
    }

    fun requestSignIn(activity: Activity) {
        activity.startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(
                    listOf(
                        AuthUI.IdpConfig.PhoneBuilder().build()
                    )
                )
                .setIsSmartLockEnabled(false)
                .build(),
            RC_SIGN_IN
        )
    }

    fun requestSignOut(activity: Activity, errorListener: () -> Unit) {
        AuthUI.getInstance().signOut(activity).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                val intent = Intent(
                    activity,
                    MainActivity::class.java
                )
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                activity.startActivity(intent)
            } else {
                errorListener()
            }
        }
    }

    fun handleAuthActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        listener: (isSuccess: Boolean, errorMessageRId: Int?) -> Unit
    ) {
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                listener(true, null)
            } else { // Sign in failed
                if (response == null) { // User pressed back button
                    listener(false, R.string.sign_cancelled)
                    return
                }
                if (response.error!!.errorCode == ErrorCodes.NO_NETWORK) {
                    listener(false, R.string.no_internet_connection)
                    return
                }
                listener(false, R.string.unkown_error)
                Log.e(TAG, "Sign-in error: ", response.error)
            }
        }
    }
}