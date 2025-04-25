package com.example.contact.data

data class Contact(
    val id: String = System.currentTimeMillis().toString(),
    val firstName: String,
    val middleName: String,
    val lastName: String,
    val contactNumber: String,
    val email: String,
    val avatarColor: Int,
    val isBlocked: Boolean = false
)