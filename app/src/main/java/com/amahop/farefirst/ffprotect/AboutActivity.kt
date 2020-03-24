package com.amahop.farefirst.ffprotect

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import com.amahop.farefirst.ffprotect.utils.*
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.activity_home.toolbarBg
import kotlinx.android.synthetic.main.view_app_bar.*
import kotlinx.android.synthetic.main.view_built_by.*

class AboutActivity : BaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        setupViews()
    }

    private fun setupViews() {
        setSupportActionBar(toolbar)
        configureAppBar()
        setupAppVersion()

        lcvRate.setOnClickListener(this)
        lcvFaq.setOnClickListener(this)
        lcvPrivacyPolicy.setOnClickListener(this)
        lcvTerms.setOnClickListener(this)
        lcvProjectSource.setOnClickListener(this)
        cvBuiltBy.setOnClickListener(this)
    }

    private fun setupAppVersion() {
        tvVersion.setText(R.string.version);

        if (AppUtils.isProduction()) {
            tvVersion.text = String.format(getString(R.string.version), BuildConfig.VERSION_NAME)
        } else {
            tvVersion.text = String.format(
                getString(R.string.version),
                BuildConfig.VERSION_NAME + " (" + BuildConfig.BUILD_TYPE + ")"
            );
        }
    }

    private fun configureAppBar() {
        supportActionBar?.let { actionBar ->
            toolbarBg?.let { tBg ->
                AppBarConfigurer.initialize(this, actionBar, tBg)
                    .setTitle(R.string.about)
                    .enableHomeAsUp()
                    .apply()
            }
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.about_menu, menu)

        if (menu is MenuBuilder) {
            menu.setOptionalIconsVisible(true)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.signOut -> {
                onClickSignOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun onClickSignOut() {
        AuthManger.requestSignOut(this) {
            val messageRId = if (it) {
                R.string.success_sign_out
            } else {
                R.string.failed_to_sign_out
            }

            Toast.makeText(this, messageRId, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.lcvRate -> onClickRateTheApp()
            R.id.lcvFaq -> onClickFAQ()
            R.id.lcvPrivacyPolicy -> onClickPrivacyPolicy()
            R.id.lcvTerms -> onClickTermsAndConditions()
            R.id.lcvProjectSource -> onClickProjectSource()
            R.id.cvBuiltBy -> onClickBuiltBy()
        }
    }

    private fun onClickProjectSource() {
        BrowserUtils.openInChromeTabOrExternalBrowser(
            this,
            RemoteConfigManager.getProjectSourceUrl()
        )
    }

    private fun onClickBuiltBy() {
        BrowserUtils.openInChromeTabOrExternalBrowser(
            this,
            RemoteConfigManager.getGotoFareFirstUrl()
        )
    }

    private fun onClickTermsAndConditions() {
        BrowserUtils.openInChromeTabOrExternalBrowser(
            this,
            RemoteConfigManager.getTermsUrl()
        )
    }

    private fun onClickPrivacyPolicy() {
        BrowserUtils.openInChromeTabOrExternalBrowser(
            this,
            RemoteConfigManager.getPrivacyUrl()
        )
    }

    private fun onClickFAQ() {
        BrowserUtils.openInChromeTabOrExternalBrowser(
            this,
            RemoteConfigManager.getFAQUrl()
        )
    }

    private fun onClickRateTheApp() {
        SystemUtils.openPlayStore(this)
    }
}
