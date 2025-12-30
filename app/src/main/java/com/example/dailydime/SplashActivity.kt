package com.example.dailydime

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide status bar and make full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.activity_splash)

        // Load animations
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)

        // Get views
        val logo = findViewById<ImageView>(R.id.splashLogoImageView)
        val appName = findViewById<TextView>(R.id.appNameTextView)
        val subtitle = findViewById<TextView>(R.id.subtitleTextView)

        // Start animations
        logo.startAnimation(fadeIn)
        appName.startAnimation(slideUp)
        subtitle.startAnimation(slideUp)

        // Navigate to LoginActivity after delay
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = android.content.Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()

            // Add transition animation
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }, 2000) // 2 seconds delay
    }
}