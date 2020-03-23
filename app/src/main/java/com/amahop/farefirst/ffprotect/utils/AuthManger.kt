package com.amahop.farefirst.ffprotect.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.amahop.farefirst.ffprotect.MainActivity
import com.amahop.farefirst.ffprotect.R
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object AuthManger {
    private const val RC_SIGN_IN = 1001 // Should be globally unique
    private const val TAG = "AuthManger"

    fun getCurrentUser(): FirebaseUser? {
        val auth = FirebaseAuth.getInstance()
        return auth.currentUser
    }

    fun isSignedIn(): Boolean {
        return getCurrentUser() != null
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
                .setTheme(R.style.AuthTheme)
                .setLogo(R.drawable.ic_ff_protect_wrting_logo)
                .setTosAndPrivacyPolicyUrls(
                    RemoteConfigManager.getTermsUrl(),
                    RemoteConfigManager.getPrivacyUrl()
                )
                .build(),
            RC_SIGN_IN
        )
    }

    fun requestSignOut(activity: Activity, errorListener: () -> Unit) {
        AuthUI.getInstance().signOut(activity).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                WorkerHelper.cancelAllPeriodicWorkers(
                    activity
                )
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
        context: Context,
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        listener: (isSuccess: Boolean, errorMessageRId: Int?) -> Unit
    ) {
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                getCurrentUser()?.let { currentUser ->
                    FirebaseAnalytics.getInstance(context).setUserId(currentUser.uid)
                }
                listener(true, null)
            } else { // Sign in failed
                if (response == null) { // User pressed back button
                    listener(
                        false,
                        R.string.sign_cancelled
                    )
                    return
                }
                if (response.error!!.errorCode == ErrorCodes.NO_NETWORK) {
                    listener(
                        false,
                        R.string.no_internet_connection
                    )
                    return
                }
                listener(
                    false,
                    R.string.unknown_error
                )
                LogManager.e(TAG, "Sign-in error: ", response.error)
            }
        }
    }

    fun getBearerToken(forceRefresh: Boolean = false, listener: (bearerToken: String?) -> Unit) {
        getCurrentUser()?.let { currentUser ->
            currentUser.getIdToken(forceRefresh).addOnCompleteListener {
                val token = it.result?.token
                if (token != null) {
                    listener("Bearer $token")
                } else {
                    listener(null)
                }
            }
        }
    }
}