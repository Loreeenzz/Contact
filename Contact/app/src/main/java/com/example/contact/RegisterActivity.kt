package com.example.contact

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.contact.data.UserRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Find views
        val usernameInput = findViewById<TextInputEditText>(R.id.usernameInput)
        val passwordInput = findViewById<TextInputEditText>(R.id.passwordInput)
        val registerButton = findViewById<MaterialButton>(R.id.registerButton)
        val backButton = findViewById<ImageView>(R.id.backButton)
        val loginNowButton = findViewById<TextView>(R.id.loginNowButton)

        // Set click listener for register button
        registerButton.setOnClickListener {
            val username = usernameInput.text?.toString()?.trim() ?: ""
            val password = passwordInput.text?.toString()?.trim() ?: ""

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (UserRepository.registerUser(username, password)) {
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                // Automatically login after successful registration
                UserRepository.loginUser(username, password)
                startDashboardActivity()
                finish()
            } else {
                Toast.makeText(this, "Username already taken", Toast.LENGTH_SHORT).show()
            }
        }

        // Set click listener for back button
        backButton.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        // Set click listener for login now button
        loginNowButton.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    private fun startDashboardActivity() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}