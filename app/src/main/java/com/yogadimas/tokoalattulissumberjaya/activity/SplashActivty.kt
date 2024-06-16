package com.yogadimas.tokoalattulissumberjaya.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.yogadimas.tokoalattulissumberjaya.R

class SplashActivty : AppCompatActivity() {

    val lDelay = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_splash_activty)

        val animFade = AnimationUtils.loadAnimation(this, R.anim.anim_fade)
        val ivApp = findViewById<ImageView>(R.id.iv_app)
        val tvApp = findViewById<TextView>(R.id.tv_app)

        ivApp.startAnimation(animFade)
        tvApp.startAnimation(animFade)

        Handler(Looper.getMainLooper()).postDelayed({
            val goToMainActivity = Intent(this@SplashActivty, MainActivity::class.java)
            startActivity(goToMainActivity)
            finish()
        }, lDelay)

    }

}