package com.example.contact.data

object UserRepository {
    private val users = mutableMapOf<String, User>()
    private var currentUser: User? = null

    fun registerUser(username: String, password: String): Boolean {
        if (users.containsKey(username)) {
            return false // User already exists
        }
        users[username] = User(username, password)
        return true
    }

    fun loginUser(username: String, password: String): Boolean {
        val user = users[username]
        return if (user?.password == password) {
            currentUser = user
            true
        } else {
            false
        }
    }

    fun getCurrentUser(): User? = currentUser

    fun getCurrentUsername(): String = currentUser?.username ?: ""

    fun updateUsername(newUsername: String): Boolean {
        if (users.containsKey(newUsername)) {
            return false // Username already taken
        }
        currentUser?.let { user ->
            users.remove(user.username)
            val updatedUser = User(newUsername, user.password)
            users[newUsername] = updatedUser
            currentUser = updatedUser
            return true
        }
        return false
    }

    fun updateUserCredentials(newUsername: String, newPassword: String): Boolean {
        if (newUsername != currentUser?.username && users.containsKey(newUsername)) {
            return false // New username already taken
        }
        currentUser?.let { user ->
            users.remove(user.username)
            val updatedUser = User(newUsername, newPassword)
            users[newUsername] = updatedUser
            currentUser = updatedUser
            return true
        }
        return false
    }

    fun logoutUser() {
        currentUser = null
    }

    fun isUserLoggedIn(): Boolean = currentUser != null
}