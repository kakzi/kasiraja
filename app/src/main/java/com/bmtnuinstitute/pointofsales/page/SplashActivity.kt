package com.bmtnuinstitute.pointofsales.page

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.bmtnuinstitute.pointofsales.R
import com.bmtnuinstitute.pointofsales.page.home.HomeActivity
import com.bmtnuinstitute.pointofsales.page.login.LoginActivity
import com.bmtnuinstitute.pointofsales.page.menu.MenuActivity
import com.bmtnuinstitute.pointofsales.page.report.ReportActivity
import com.bmtnuinstitute.pointofsales.preferences.PrefManager

class SplashActivity : AppCompatActivity() {

    private val splashDelay: Long = 3000
    private val pref by lazy { PrefManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        setupView()
    }

    override fun onStart() {
        super.onStart()
        Handler().postDelayed({
            if (pref.getBoolean("pref_user_login")) {
                when (pref.getString("pref_user_level")) {
                    "kasir" -> startActivity(Intent(this, MenuActivity::class.java))
                    "owner" -> startActivity(Intent(this, ReportActivity::class.java))
                }
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, splashDelay)
    }

    private fun setupView(){
        supportActionBar!!.hide()
    }
}
