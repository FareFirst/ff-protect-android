package com.amahop.farefirst.ffprotect

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.amahop.farefirst.ffprotect.tracker.TrackerManager
import com.amahop.farefirst.ffprotect.tracker.TrackerStatusObserver
import com.amahop.farefirst.ffprotect.ui.dashboard.DashboardViewModel
import com.amahop.farefirst.ffprotect.utils.*
import com.amahop.farefirst.ffprotect.utils.bluetooth.BluetoothHelper
import com.amahop.farefirst.ffprotect.utils.bluetooth.BluetoothStatusChangeObserver
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.view_app_bar.*
import kotlinx.android.synthetic.main.view_built_by.*

class HomeActivity : BaseActivity(), View.OnClickListener {

    companion object {
        const val TAG = "HomeActivity"

        fun handleShowHomeActivity(activity: BaseActivity) {
            var clazz: Class<*> = HomeActivity::class.java
            if (AuthManger.isSignedIn()) {
                if (!PermissionHelper.isLocationPermissionGranted(activity)) {
                    clazz = PermissionActivity::class.java
                }
            } else {
                clazz = MainActivity::class.java
            }

            val intent = Intent(activity, clazz)
            activity.startActivity(intent)
            activity.finish()
        }
    }

    private lateinit var trackerManager: TrackerManager
    private var isTrackerManagerStarted = false

    private val model: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        trackerManager = TrackerManager(this)
        setupViews()
        startTracker()
    }

    private fun startTracker() {
        if (isTrackerManagerStarted) {
            LogManager.w(TAG, "TrackerManager already started")
            return
        }

        if (!BluetoothHelper.isBluetoothEnabled()) {
            Log.d(TAG, "Skipping startTracker as Bluetooth not enabled")
            return
        }

        if (!Settings.isTrackerOn()) {
            LogManager.w(TAG, "Skipping startTracker as tracking is off")
            return
        }

        WorkerHelper.scheduleAllPeriodicWorkers(this)

        try {
            trackerManager.start(TAG, true)
            isTrackerManagerStarted = true
            Log.d(TAG, "startTracker  => Success")
        } catch (th: Throwable) {
            LogManager.e(TAG, th.message, th)
        }
    }

    private fun stopTracker() {
        isTrackerManagerStarted = false
        trackerManager.stop(TAG)
    }

    private fun setupViews() {
        setSupportActionBar(toolbar)
        configureAppBar()

        btnShare.setOnClickListener(this)
        cvBuiltBy.setOnClickListener(this)

        setupBluetoothDisabledCard()
        setupTrackerOffCard()
        setupHowItWorks()
        setupSwipeToRefreshView()
        setupTrackerInfo()
    }

    private fun setupTrackerOffCard() {
        cvTrackerOff.onEnableClicked {
            Settings.setIsTrackerOn(true)
            model.refreshAll()
        }
        model.isTrackerOn.observe(this, Observer {
            if (it) {
                cvTrackerOff.visibility = View.GONE
            } else {
                cvTrackerOff.visibility = View.VISIBLE
            }

            handleTrackerStartStop(it)
        })
    }

    private fun handleTrackerStartStop(newDependancyValue: Boolean) {
        if (isTrackerManagerStarted && !newDependancyValue) {
            stopTracker()
        } else if (!isTrackerManagerStarted && newDependancyValue) {
            startTracker()
        }
    }

    private fun setupTrackerInfo() {
        model.isTrackerRunning.observe(this, Observer {
            trackerInfoView.setIsTrackerRunning(it)
        })
        model.lastSyncedAt.observe(this, Observer {
            trackerInfoView.setLastSyncedAt(it)
        })
        model.phoneNumber.observe(this, Observer {
            trackerInfoView.setPhoneNumber(it)
        })
        model.isGovMessageFetchLoading.observe(this, Observer {
            trackerInfoView.setIsLoading(it)
        })
        model.isGovMessageFetchFailed.observe(this, Observer {
            trackerInfoView.setIsError(it)
        })
        model.govMessage.observe(this, Observer {
            trackerInfoView.setMessage(it)
        })

        trackerInfoView.setOnRetryClickListener {
            model.fetchGovMessage()
        }

        TrackerStatusObserver(trackerManager) {
            model.setIsTrackerRunning(it)
        }.registerLifecycle(lifecycle)
    }

    private fun setupHowItWorks() {
        icHowItWorks.onKnowMoreClicked {
            BrowserUtils.openInChromeTabOrExternalBrowser(
                this,
                RemoteConfigManager.getHowItWorksUrl()
            )
        }
    }

    private fun setupBluetoothDisabledCard() {
        cvBluetoothOff.onEnableClicked {
            onClickTurnOnBluetooth()
        }

        model.isBluetoothOn.observe(this, Observer {
            it?.let {
                cvBluetoothOff.visibility = if (it) {
                    View.GONE
                } else {
                    View.VISIBLE
                }

                handleTrackerStartStop(it)
            }
        })
        BluetoothStatusChangeObserver(this) {
            Log.d(TAG, "Status changed")
            model.refreshBluetoothStatus()
        }.registerLifecycle(lifecycle)
    }


    private fun setupSwipeToRefreshView() {
        srLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccent))
        srLayout.setOnRefreshListener {
            model.refreshAll()
        }
        model.isLoading.observe(this, Observer {
            srLayout.isRefreshing = it
        })
    }

    private fun configureAppBar() {
        supportActionBar?.let { actionBar ->
            toolbarBg?.let { tBg ->
                AppBarConfigurer.initialize(this, actionBar, tBg)
                    .setLogo(R.drawable.ic_ff_protect_wrting_shield_logo)
                    .apply()
            }
        }
    }

    override fun onClick(v: View?) {
        if (v == null) return

        when (v.id) {
            R.id.btnShare -> onClickShareButton()
            R.id.cvBuiltBy -> onClickGotoFareFirst()
        }
    }

    private fun onClickGotoFareFirst() {
        BrowserUtils.openInChromeTabOrExternalBrowser(
            this,
            RemoteConfigManager.getGotoFareFirstUrl()
        )
    }

    private fun onClickShareButton() {
        ExternalActionHelper.shareApp(this)
    }

    private fun onClickTurnOnBluetooth() {
        startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
    }

//    private fun onClickSignOut() {
//        btnSignOut.isEnabled = false
//        btnSignOut.setText(R.string.please_wait)
//        AuthManger.requestSignOut(this) {
//            btnSignOut?.isEnabled = true
//            btnSignOut?.setText(R.string.sign_out)
//            Toast.makeText(
//                this,
//                R.string.failed_to_sign_out,
//                Toast.LENGTH_SHORT
//            ).show()
//        }
//    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                showSettingsActivity()
                true
            }
            R.id.about -> {
                Toast.makeText(this, "Show about", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSettingsActivity() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        stopTracker()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        model.refreshAll()
    }
}
