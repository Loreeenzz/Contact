package com.example.contact

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.contact.data.Contact
import com.example.contact.data.ContactRepository
import com.example.contact.data.UserRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class DashboardActivity : Activity() {
    private lateinit var contactsAdapter: ContactsAdapter
    private lateinit var emptyStateView: LinearLayout
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Initialize repositories
        ContactRepository.initialize(this)

        // Check if user is logged in
        if (!UserRepository.isUserLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Find views
        recyclerView = findViewById(R.id.contactsRecyclerView)
        emptyStateView = findViewById(R.id.emptyStateView)

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        contactsAdapter = ContactsAdapter { contact, view ->
            showPopupMenu(view, contact)
        }
        recyclerView.adapter = contactsAdapter

        // Set up create contact button
        val createContactButton = findViewById<MaterialButton>(R.id.createContactButton)
        createContactButton.setOnClickListener {
            startActivity(Intent(this, AddContactActivity::class.java))
        }

        // Set up bottom navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.nav_home
        bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    // Already on home
                    true
                }
                R.id.nav_add -> {
                    startActivity(Intent(this, BlockActivity::class.java))
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

        // Load initial contacts
        updateContactsList()
    }

    override fun onResume() {
        super.onResume()
        // Refresh contacts list
        updateContactsList()
    }

    private fun updateContactsList() {
        // Only show unblocked contacts
        val unblockContacts = ContactRepository.getAllContacts().filter { !it.isBlocked }
        contactsAdapter.updateContacts(unblockContacts)

        // Show/hide empty state
        if (unblockContacts.isEmpty()) {
            emptyStateView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyStateView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun showPopupMenu(view: android.view.View, contact: Contact) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.contact_more_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.action_edit -> {
                    val intent = Intent(this, EditContactActivity::class.java).apply {
                        putExtra("contactId", contact.id)
                        putExtra("firstName", contact.firstName)
                        putExtra("middleName", contact.middleName)
                        putExtra("lastName", contact.lastName)
                        putExtra("contactNumber", contact.contactNumber)
                        putExtra("email", contact.email)
                        putExtra("avatarColor", contact.avatarColor)
                        putExtra("isBlocked", contact.isBlocked)
                    }
                    startActivity(intent)
                    true
                }
                R.id.action_block -> {
                    blockContact(contact)
                    true
                }
                else -> false
            }
        }

        // Apply Poppins font to popup menu
        try {
            val field = PopupMenu::class.java.getDeclaredField("mPopup")
            field.isAccessible = true
            val menuPopupHelper = field.get(popup)
            menuPopupHelper?.javaClass?.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                ?.invoke(menuPopupHelper, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        popup.show()
    }

    private fun blockContact(contact: Contact) {
        // Update contact in repository
        val updatedContact = contact.copy(isBlocked = true)
        ContactRepository.updateContact(updatedContact)

        // Update UI immediately
        updateContactsList()

        // Show toast and navigate to block screen
        Toast.makeText(this, "${contact.firstName} has been blocked", Toast.LENGTH_SHORT).show()

        // Navigate to block screen
        val intent = Intent(this, BlockActivity::class.java)
        startActivity(intent)
    }

    private class ContactsAdapter(
        private val onMoreClick: (Contact, View) -> Unit
    ) : RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

        private var contacts: List<Contact> = emptyList()

        fun updateContacts(newContacts: List<Contact>) {
            contacts = newContacts
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_contact, parent, false)
            return ContactViewHolder(view)
        }

        override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
            val contact = contacts[position]
            holder.bind(contact)
        }

        override fun getItemCount() = contacts.size

        inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val avatar: ImageView = itemView.findViewById(R.id.contactAvatar)
            private val name: TextView = itemView.findViewById(R.id.contactName)
            private val moreButton: ImageView = itemView.findViewById(R.id.moreButton)

            fun bind(contact: Contact) {
                // Create a circular background with the contact's color
                val background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(contact.avatarColor)
                }
                avatar.background = background

                name.text = "${contact.firstName} ${contact.lastName}"
                moreButton.setOnClickListener { onMoreClick(contact, it) }
            }
        }
    }
}