package com.example.dailydime


import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var emailLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signUpTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initializeViews()
        setupListeners()

        // Check if user is already logged in
        if (isUserLoggedIn()) {
            redirectToMainPage()
        }
    }

    private fun initializeViews() {
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)

        emailLayout = findViewById(R.id.emailLayout)
        passwordLayout = findViewById(R.id.passwordLayout)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        signUpTextView = findViewById(R.id.signUpTextView)
    }

    private fun setupListeners() {
        // Clear errors on focus
        emailEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) emailLayout.error = null
        }

        passwordEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) passwordLayout.error = null
        }

        loginButton.setOnClickListener {
            if (validateInputs()) {
                attemptLogin()
            }
        }

        signUpTextView.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString()

        // Email validation
        when {
            email.isEmpty() -> {
                emailLayout.error = "Email is required"
                isValid = false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                emailLayout.error = "Please enter a valid email address"
                isValid = false
            }
        }

        // Password validation
        when {
            password.isEmpty() -> {
                passwordLayout.error = "Password is required"
                isValid = false
            }
            password.length < 8 -> {
                passwordLayout.error = "Password must be at least 8 characters"
                isValid = false
            }
        }

        return isValid
    }

    private fun attemptLogin() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString()

        val storedEmail = sharedPreferences.getString("email", null)
        val storedPassword = sharedPreferences.getString("password", null)

        if (email == storedEmail && password == storedPassword) {
            loginSuccess(email)
        } else {
            loginFailed()
        }
    }

    private fun loginSuccess(email: String) {
        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
        saveUserSession(email)
        redirectToMainPage()
    }

    private fun loginFailed() {
        // Clear password field
        passwordEditText.text.clear()

        // Show error in password field
        passwordLayout.error = "Invalid email or password"

        // Shake animation for the login button
        loginButton.startAnimation(android.view.animation.AnimationUtils.loadAnimation(
            this, R.anim.shake
        ))

        Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
    }

    private fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    private fun saveUserSession(email: String) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", true)
        editor.putString("loggedInUserEmail", email)
        editor.apply()
    }

    private fun redirectToMainPage() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}