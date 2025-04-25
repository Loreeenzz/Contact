package com.example.contact

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
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
import com.google.android.material.bottomnavigation.BottomNavigationView

class BlockActivity : Activity() {
    private lateinit var contactsAdapter: BlockedContactsAdapter
    private lateinit var emptyStateView: LinearLayout
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block)

        // Initialize repository
        ContactRepository.initialize(this)

        // Find views
        recyclerView = findViewById(R.id.blockedContactsRecyclerView)
        emptyStateView = findViewById(R.id.emptyStateView)

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        contactsAdapter = BlockedContactsAdapter { contact ->
            unblockContact(contact)
        }
        recyclerView.adapter = contactsAdapter

        // Set up bottom navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.nav_add // Set add button as selected
        bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_add -> {
                    // Already on block screen
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

        // Load blocked contacts
        updateBlockedContacts()
    }

    private fun updateBlockedContacts() {
        val blockedContacts = ContactRepository.getAllContacts().filter { it.isBlocked }
        contactsAdapter.updateContacts(blockedContacts)

        // Show/hide empty state
        if (blockedContacts.isEmpty()) {
            emptyStateView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyStateView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun unblockContact(contact: Contact) {
        val updatedContact = contact.copy(isBlocked = false)
        ContactRepository.updateContact(updatedContact)
        Toast.makeText(this, "${contact.firstName} has been unblocked", Toast.LENGTH_SHORT).show()
        updateBlockedContacts()
    }

    private class BlockedContactsAdapter(
        private val onUnblock: (Contact) -> Unit
    ) : RecyclerView.Adapter<BlockedContactsAdapter.ContactViewHolder>() {

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

                moreButton.setOnClickListener { view ->
                    showPopupMenu(view, contact)
                }
            }

            private fun showPopupMenu(view: View, contact: Contact) {
                val popup = PopupMenu(view.context, view)
                popup.menuInflater.inflate(R.menu.blocked_contact_menu, popup.menu)

                popup.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_unblock -> {
                            onUnblock(contact)
                            true
                        }
                        else -> false
                    }
                }

                popup.show()
            }
        }
    }
} 