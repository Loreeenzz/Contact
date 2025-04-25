package com.example.contact

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.example.contact.data.UserRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class ProfileActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Set up edit profile button
        val editProfileButton = findViewById<MaterialButton>(R.id.editProfileButton)
        editProfileButton.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // Set up logout button
        val logoutButton = findViewById<MaterialButton>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            UserRepository.logoutUser()
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }

        // Set up bottom navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.nav_profile
        bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    if (menuItem.itemId != bottomNav.selectedItemId) {
                        startActivity(Intent(this, DashboardActivity::class.java))
                        finish()
                    }
                    true
                }
                R.id.nav_add -> {
                    if (menuItem.itemId != bottomNav.selectedItemId) {
                        startActivity(Intent(this, BlockActivity::class.java))
                        finish()
                    }
                    true
                }
                R.id.nav_profile -> {
                    // Already on profile
                    true
                }
                else -> false
            }
        }
    }
}