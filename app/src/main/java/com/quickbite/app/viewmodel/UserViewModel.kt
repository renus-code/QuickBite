package com.quickbite.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickbite.app.data.GiftCardRepository
import com.quickbite.app.data.UserRepository
import com.quickbite.app.model.GiftCard
import com.quickbite.app.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class UserViewModel(
    private val repository: UserRepository,
    private val giftCardRepository: GiftCardRepository
) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    private val _currentUser = MutableStateFlow<User?>(null)
    private val _isLoggedIn = MutableStateFlow(false)
    private val _darkModeEnabled = MutableStateFlow(false)
    
    // Feedback message for UI
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> get() = _message

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
    }
    
    fun clearMessage() {
        _message.value = null
    }

    suspend fun signup(email: String, password: String, displayName: String): Boolean {
        val existingUser = repository.getUserByEmail(email)
        if (existingUser == null) {
            val newUser = User(email, password, displayName)
            repository.insertUser(newUser)
            loadUsers() // Refresh the user list
            return true
        }
        return false
    }

    suspend fun login(email: String, password: String): Boolean {
        val existingUser = repository.getUserByEmail(email)
        if (existingUser != null && existingUser.password == password) {
            _currentUser.value = existingUser
            _isLoggedIn.value = true
            return true
        }
        return false
    }

    fun logout() {
        _currentUser.value = null
        _isLoggedIn.value = false
    }

    // --- Gift Card Logic ---

    fun purchaseGiftCard(
        amount: Double,
        senderName: String,
        recipientName: String,
        recipientEmail: String
    ) {
        viewModelScope.launch {
            val code = UUID.randomUUID().toString().substring(0, 8).uppercase()
            val giftCard = GiftCard(
                code = code,
                amount = amount,
                senderName = senderName,
                recipientName = recipientName,
                recipientEmail = recipientEmail
            )
            giftCardRepository.createGiftCard(giftCard)
            _message.value = "Gift Card Purchased! Code: $code"
        }
    }

    fun redeemGiftCard(code: String) {
        viewModelScope.launch {
            val giftCard = giftCardRepository.getGiftCardByCode(code)
            if (giftCard != null) {
                if (!giftCard.isRedeemed) {
                    val updatedGiftCard = giftCard.copy(
                        isRedeemed = true,
                        redeemedByUserId = _currentUser.value?.id // Link to current user if logged in
                    )
                    giftCardRepository.updateGiftCard(updatedGiftCard)
                    _message.value = "Success! $${giftCard.amount} added to your account."
                    // TODO: Update actual user balance here if/when User entity supports it
                } else {
                    _message.value = "This gift card has already been redeemed."
                }
            } else {
                _message.value = "Invalid gift card code."
            }
        }
    }
}
