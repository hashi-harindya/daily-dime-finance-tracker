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

class SignUpActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var usernameLayout: TextInputLayout
    private lateinit var emailLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var confirmPasswordLayout: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)

        // Initialize TextInputLayouts
        usernameLayout = findViewById(R.id.usernameLayout)
        emailLayout = findViewById(R.id.emailLayout)
        passwordLayout = findViewById(R.id.passwordLayout)
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout)

        val usernameEditText = findViewById<EditText>(R.id.usernameEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val confirmPasswordEditText = findViewById<EditText>(R.id.confirmPasswordEditText)
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        val loginTextView = findViewById<TextView>(R.id.loginTextView)

        // Clear errors on text change
        usernameEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) usernameLayout.error = null
        }
        emailEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) emailLayout.error = null
        }
        passwordEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) passwordLayout.error = null
        }
        confirmPasswordEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) confirmPasswordLayout.error = null
        }

        signUpButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (validateInputs(username, email, password, confirmPassword)) {
                // Check if username already exists
                if (sharedPreferences.getString("username", "") == username) {
                    usernameLayout.error = "Username already exists"
                    return@setOnClickListener
                }

                // Check if email already exists
                if (sharedPreferences.getString("email", "") == email) {
                    emailLayout.error = "Email already registered"
                    return@setOnClickListener
                }

                // Save user data
                val editor = sharedPreferences.edit()
                editor.putString("username", username)
                editor.putString("email", email)
                editor.putString("password", password)
                editor.apply()

                Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        loginTextView.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun validateInputs(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        var isValid = true

        // Username validation
        when {
            username.isEmpty() -> {
                usernameLayout.error = "Username is required"
                isValid = false
            }
            username.length < 3 -> {
                usernameLayout.error = "Username must be at least 3 characters long"
                isValid = false
            }
            !username.matches(Regex("^[a-zA-Z0-9._-]+$")) -> {
                usernameLayout.error = "Username can only contain letters, numbers, dots, underscores and hyphens"
                isValid = false
            }
        }

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
                passwordLayout.error = "Password must be at least 8 characters long"
                isValid = false
            }
            !password.matches(Regex(".*[A-Z].*")) -> {
                passwordLayout.error = "Password must contain at least one uppercase letter"
                isValid = false
            }
            !password.matches(Regex(".*[a-z].*")) -> {
                passwordLayout.error = "Password must contain at least one lowercase letter"
                isValid = false
            }
            !password.matches(Regex(".*[0-9].*")) -> {
                passwordLayout.error = "Password must contain at least one number"
                isValid = false
            }
            !password.matches(Regex(".*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) -> {
                passwordLayout.error = "Password must contain at least one special character"
                isValid = false
            }
        }

        // Confirm Password validation
        when {
            confirmPassword.isEmpty() -> {
                confirmPasswordLayout.error = "Please confirm your password"
                isValid = false
            }
            confirmPassword != password -> {
                confirmPasswordLayout.error = "Passwords do not match"
                isValid = false
            }
        }

        return isValid
    }
}