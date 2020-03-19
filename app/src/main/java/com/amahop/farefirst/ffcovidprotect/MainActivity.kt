package com.amahop.farefirst.ffcovidprotect

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.PhoneBuilder
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import java.util.*


class MainActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        const val TAG = "MainActivity"
        const val RC_SIGN_IN = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handleAuth()
    }

    private fun handleAuth() {
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            showMainScreen()
        }
    }

    private fun showSignInScreen() {
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(
                    Arrays.asList(
                        PhoneBuilder().build()
                    )
                )
                .build(),
            RC_SIGN_IN
        )
    }

    private fun showMainScreen() {
        showMessage(R.string.welcome_home)
        finish()
    }

    private fun showMessage(rId: Int) {
        Toast.makeText(this, rId, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                showMainScreen()
            } else { // Sign in failed
                if (response == null) { // User pressed back button
                    showMessage(R.string.sign_cancelled)
                    return
                }
                if (response.error!!.errorCode == ErrorCodes.NO_NETWORK) {
                    showMessage(R.string.no_internet_connection)
                    return
                }
                showMessage(R.string.unkown_error)
                Log.e(TAG, "Sign-in error: ", response.error)
            }
        }
    }

    override fun onClick(v: View?) {
        if (v == null) return

        when (v.id) {
            R.id.btnSignIn -> showSignInScreen()
        }
    }
}
