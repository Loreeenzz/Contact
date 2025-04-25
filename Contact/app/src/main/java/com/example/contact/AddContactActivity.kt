package com.example.contact

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import com.example.contact.data.Contact
import com.example.contact.data.ContactRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class AddContactActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        // Initialize ContactRepository
        ContactRepository.initialize(this)

        // Find views
        val backButton = findViewById<ImageView>(R.id.backButton)
        val firstNameInput = findViewById<TextInputEditText>(R.id.firstNameInput)
        val middleNameInput = findViewById<TextInputEditText>(R.id.middleNameInput)
        val lastNameInput = findViewById<TextInputEditText>(R.id.lastNameInput)
        val contactNumberInput = findViewById<TextInputEditText>(R.id.contactNumberInput)
        val emailInput = findViewById<TextInputEditText>(R.id.emailInput)
        val addContactButton = findViewById<MaterialButton>(R.id.addContactButton)

        // Set up back button
        backButton.setOnClickListener {
            finish()
        }

        // Set up add contact button
        addContactButton.setOnClickListener {
            val firstName = firstNameInput.text?.toString()?.trim() ?: ""
            val middleName = middleNameInput.text?.toString()?.trim() ?: ""
            val lastName = lastNameInput.text?.toString()?.trim() ?: ""
            val contactNumber = contactNumberInput.text?.toString()?.trim() ?: ""
            val email = emailInput.text?.toString()?.trim() ?: ""

            if (firstName.isEmpty() || lastName.isEmpty() || contactNumber.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create and save contact
            val contact = Contact(
                firstName = firstName,
                middleName = middleName,
                lastName = lastName,
                contactNumber = contactNumber,
                email = email,
                avatarColor = 0, // Will be set by repository
                isBlocked = false
            )

            ContactRepository.addContact(contact)
            Toast.makeText(this, "Contact added successfully", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Set up bottom navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.nav_add
        bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    finish()
                    true
                }
                R.id.nav_add -> {
                    // Already on add
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}