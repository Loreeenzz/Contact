package com.example.contact

import android.app.Activity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import com.example.contact.data.Contact
import com.example.contact.data.ContactRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class EditContactActivity : Activity() {
    private var contactId: String? = null
    private var avatarColor: Int = 0
    private var isBlocked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_contact)

        // Initialize repository
        ContactRepository.initialize(this)

        // Find views
        val backButton = findViewById<ImageView>(R.id.backButton)
        val firstNameInput = findViewById<TextInputEditText>(R.id.firstNameInput)
        val middleNameInput = findViewById<TextInputEditText>(R.id.middleNameInput)
        val lastNameInput = findViewById<TextInputEditText>(R.id.lastNameInput)
        val contactNumberInput = findViewById<TextInputEditText>(R.id.contactNumberInput)
        val emailInput = findViewById<TextInputEditText>(R.id.emailInput)
        val saveButton = findViewById<MaterialButton>(R.id.saveButton)

        // Get contact data from intent
        contactId = intent.getStringExtra("contactId")
        val firstName = intent.getStringExtra("firstName") ?: ""
        val middleName = intent.getStringExtra("middleName") ?: ""
        val lastName = intent.getStringExtra("lastName") ?: ""
        val contactNumber = intent.getStringExtra("contactNumber") ?: ""
        val email = intent.getStringExtra("email") ?: ""
        avatarColor = intent.getIntExtra("avatarColor", 0)
        isBlocked = intent.getBooleanExtra("isBlocked", false)

        // Set existing contact data to fields
        firstNameInput.setText(firstName)
        middleNameInput.setText(middleName)
        lastNameInput.setText(lastName)
        contactNumberInput.setText(contactNumber)
        emailInput.setText(email)

        // Set up back button
        backButton.setOnClickListener {
            finish()
        }

        // Set up save button
        saveButton.setOnClickListener {
            val newFirstName = firstNameInput.text?.toString()?.trim() ?: ""
            val newMiddleName = middleNameInput.text?.toString()?.trim() ?: ""
            val newLastName = lastNameInput.text?.toString()?.trim() ?: ""
            val newContactNumber = contactNumberInput.text?.toString()?.trim() ?: ""
            val newEmail = emailInput.text?.toString()?.trim() ?: ""

            if (newFirstName.isEmpty() || newLastName.isEmpty() || newContactNumber.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Update contact in repository
            contactId?.let { id ->
                val updatedContact = Contact(
                    id = id,
                    firstName = newFirstName,
                    middleName = newMiddleName,
                    lastName = newLastName,
                    contactNumber = newContactNumber,
                    email = newEmail,
                    avatarColor = avatarColor,
                    isBlocked = isBlocked
                )
                ContactRepository.updateContact(updatedContact)
                Toast.makeText(this, "Contact updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        // Set up bottom navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    finish()
                    true
                }
                R.id.nav_add -> {
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}