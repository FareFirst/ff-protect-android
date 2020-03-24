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
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.amahop.farefirst.ffprotect.tracker.TrackerManager
import com.amahop.farefirst.ffprotect.ui.dashboard.DashboardViewModel
import com.amahop.farefirst.ffprotect.utils.*
import com.amahop.farefirst.ffprotect.utils.bluetooth.BluetoothHelper
import com.amahop.farefirst.ffprotect.utils.bluetooth.BluetoothStatusChangeObserver
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.view_app_bar.*
import kotlinx.android.synthetic.main.view_bluetooth_off_card.*

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

    private var trackerManager: TrackerManager? = null
    private var isTrackerManagerStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupViews()
        initViewModel()
        setupTracker()
    }

    private fun initViewModel() {
        val model: DashboardViewModel by viewModels()
        handleBluetoothChanges(model)
    }

    private fun handleBluetoothChanges(model: DashboardViewModel) {
        model.isBluetoothOn.observe(this, Observer {
            it?.let {
                cvBluetoothOff.visibility = if (it) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
            }
        })
        BluetoothStatusChangeObserver(this) {
            Log.d(TAG, "Status changed")
            model.refreshBluetoothStatus()
            startTracker()
        }.registerLifecycle(lifecycle)
    }

    private fun setupTracker() {
        startTracker()
        WorkerHelper.scheduleAllPeriodicWorkers(this)
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

        try {
            trackerManager = TrackerManager(this)
            trackerManager?.start(TAG, true)
            isTrackerManagerStarted = true
            Log.d(TAG, "startTracker  => Success")
        } catch (th: Throwable) {
            LogManager.e(TAG, th.message, th)
        }
    }

    private fun setupViews() {
        setSupportActionBar(toolbar)
        configureAppBar()

        btnSignOut.setOnClickListener(this)

        setupBluetoothDisabledCard()
        setupHowItWorks()
        setupSwipeToRefreshView()
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
        val drawable =
            AppCompatResources.getDrawable(this, R.drawable.ic_bluetooth_disabled_error_24dp)

        tvBluetoothDisabled.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        btnEnable.setOnClickListener(this)
    }

    private fun setupSwipeToRefreshView() {
        srLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccent))
        srLayout.setOnRefreshListener {
            refresh()
        }
    }

    private fun refresh() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
            R.id.btnSignOut -> onClickSignOut()
            R.id.btnEnable -> onClickTurnOnBluetooth()
        }
    }

    private fun onClickTurnOnBluetooth() {
        startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
    }

    private fun onClickSignOut() {
        btnSignOut.isEnabled = false
        btnSignOut.setText(R.string.please_wait)
        AuthManger.requestSignOut(this) {
            btnSignOut?.isEnabled = true
            btnSignOut?.setText(R.string.sign_out)
            Toast.makeText(
                this,
                R.string.failed_to_sign_out,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

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
        trackerManager?.stop(TAG)
        super.onDestroy()
    }
}
