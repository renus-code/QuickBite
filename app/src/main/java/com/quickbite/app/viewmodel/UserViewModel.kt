package com.quickbite.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickbite.app.data.UserRepository
import com.quickbite.app.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    private val _currentUser = MutableStateFlow<User?>(null)
    private val _isLoggedIn = MutableStateFlow(false)
    private val _darkModeEnabled = MutableStateFlow(false) // Default to false

    val user: StateFlow<User?> get() = _currentUser
    val isLoggedIn: StateFlow<Boolean> get() = _isLoggedIn
    val darkModeEnabled: StateFlow<Boolean> get() = _darkModeEnabled

    init {
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            val savedUsers = repository.getAllUsers()
            if (savedUsers.isEmpty()) {
                // Pre-populate with a default user if the database is empty
                val defaultUser = User("renu@gmail.com", "password", "Renu")
                repository.insertUser(defaultUser)
                _users.value = listOf(defaultUser)
            } else {
                _users.value = savedUsers
            }
        }
    }

    fun setDarkMode(enabled: Boolean) {
        _darkModeEnabled.value = enabled
        // You can save this to Room or DataStore if you want it to persist
    }

    fun signup(email: String, password: String, displayName: String): Boolean {
        viewModelScope.launch {
            val existingUser = repository.getUserByEmail(email)
            if (existingUser == null) {
                val newUser = User(email, password, displayName)
                repository.insertUser(newUser)
                loadUsers() // Refresh the user list
            }
        }
        return true // Assume success for now
    }

    fun login(email: String, password: String): Boolean {
        var success = false
        viewModelScope.launch {
            val existingUser = repository.getUserByEmail(email)
            if (existingUser != null && existingUser.password == password) {
                _currentUser.value = existingUser
                _isLoggedIn.value = true
                success = true
            }
        }
        return success
    }

    fun logout() {
        _currentUser.value = null
        _isLoggedIn.value = false
    }
}
