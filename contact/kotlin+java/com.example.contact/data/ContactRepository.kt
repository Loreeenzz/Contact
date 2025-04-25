package com.example.contact.data

import android.content.Context
import android.content.SharedPreferences
import java.util.*

object ContactRepository {
    private const val PREFS_NAME = "ContactPrefs"
    private const val CONTACTS_KEY = "contacts"
    private const val FIELD_SEPARATOR = "|||"
    private const val CONTACT_SEPARATOR = "###"
    
    private val colors = listOf(
        0xFFFF6B8E.toInt(), // Pink
        0xFF4CAF50.toInt(), // Green
        0xFF2196F3.toInt(), // Blue
        0xFFFF9800.toInt(), // Orange
        0xFFE91E63.toInt()  // Red
    )

    private lateinit var prefs: SharedPreferences

    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun addContact(contact: Contact): Contact {
        val contacts = getAllContacts().toMutableList()
        val newContact = contact.copy(
            avatarColor = colors[Random().nextInt(colors.size)],
            isBlocked = false
        )
        contacts.add(newContact)
        saveContacts(contacts)
        return newContact
    }

    fun updateContact(updatedContact: Contact) {
        val contacts = getAllContacts().toMutableList()
        val index = contacts.indexOfFirst { it.id == updatedContact.id }
        if (index != -1) {
            contacts[index] = updatedContact
            saveContacts(contacts)
        }
    }

    fun getContactById(id: String): Contact? {
        return getAllContacts().find { it.id == id }
    }

    fun getAllContacts(): List<Contact> {
        val contactsString = prefs.getString(CONTACTS_KEY, "") ?: ""
        if (contactsString.isEmpty()) return emptyList()

        return contactsString.split(CONTACT_SEPARATOR).mapNotNull { contactStr ->
            try {
                val fields = contactStr.split(FIELD_SEPARATOR)
                if (fields.size == 8) {
                    Contact(
                        id = fields[0],
                        firstName = fields[1],
                        middleName = fields[2],
                        lastName = fields[3],
                        contactNumber = fields[4],
                        email = fields[5],
                        avatarColor = fields[6].toIntOrNull() ?: colors[0],
                        isBlocked = fields[7].toBoolean()
                    )
                } else null
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun saveContacts(contacts: List<Contact>) {
        val contactsString = contacts.joinToString(CONTACT_SEPARATOR) { contact ->
            listOf(
                contact.id,
                contact.firstName,
                contact.middleName,
                contact.lastName,
                contact.contactNumber,
                contact.email,
                contact.avatarColor.toString(),
                contact.isBlocked.toString()
            ).joinToString(FIELD_SEPARATOR)
        }
        prefs.edit().putString(CONTACTS_KEY, contactsString).apply()
    }
} 