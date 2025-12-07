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
        // Persist to database if user is logged in
        viewModelScope.launch {
            _currentUser.value?.let { user ->
                val updatedUser = user.copy(isDarkMode = enabled)
                repository.updateUser(updatedUser)
                _currentUser.value = updatedUser
            }
        }
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
            // Apply user's dark mode preference
            _darkModeEnabled.value = existingUser.isDarkMode
            return true
        }
        return false
    }

    fun logout() {
        _currentUser.value = null
        _isLoggedIn.value = false
        // REMOVED: _darkModeEnabled.value = false 
        // We keep the current theme to prevent the "white flash" during logout transition.
        // The next user can change it if they want.
    }

    fun updateUserProfile(
        phoneNumber: String? = null,
        address: String? = null,
        paymentMethod: String? = null,
        avatarId: String? = null
    ) {
        viewModelScope.launch {
            val currentUser = _currentUser.value
            if (currentUser != null) {
                val updatedUser = currentUser.copy(
                    phoneNumber = phoneNumber ?: currentUser.phoneNumber,
                    address = address ?: currentUser.address,
                    paymentMethod = paymentMethod ?: currentUser.paymentMethod,
                    avatarId = avatarId ?: currentUser.avatarId
                )
                repository.updateUser(updatedUser)
                _currentUser.value = updatedUser
                _message.value = "Profile Updated Successfully"
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            val currentUser = _currentUser.value
            if (currentUser != null) {
                repository.deleteUser(currentUser)
                logout()
                _message.value = "Account Deleted"
            }
        }
    }

    // --- Gift Card Logic ---

    fun purchaseGiftCard(
        amount: Double,
        senderName: String,
        recipientName: String,
        recipientEmail: String,
        customCode: String? = null
    ) {
        viewModelScope.launch {
            // Use custom code if provided, otherwise generate one
            val code = customCode ?: UUID.randomUUID().toString().substring(0, 8).uppercase()
            val giftCard = GiftCard(
                code = code,
                amount = amount,
                senderName = senderName,
                recipientName = recipientName,
                recipientEmail = recipientEmail
            )
            giftCardRepository.createGiftCard(giftCard)
        }
    }

    fun redeemGiftCard(code: String) {
        viewModelScope.launch {
            val giftCard = giftCardRepository.getGiftCardByCode(code)
            val currentUser = _currentUser.value

            if (giftCard != null) {
                if (currentUser != null) {
                    // Security Check: Verify recipient email
                    if (giftCard.recipientEmail.equals(currentUser.email, ignoreCase = true)) {
                        if (!giftCard.isRedeemed) {
                            val updatedGiftCard = giftCard.copy(
                                isRedeemed = true,
                                redeemedByUserId = currentUser.id
                            )
                            giftCardRepository.updateGiftCard(updatedGiftCard)
                            
                            // Update User Balance
                            val newBalance = currentUser.balance + giftCard.amount
                            val updatedUser = currentUser.copy(balance = newBalance)
                            repository.updateUser(updatedUser)
                            _currentUser.value = updatedUser

                            _message.value = "Success! $${giftCard.amount} added to your account."

                        } else {
                            _message.value = "This gift card has already been redeemed."
                        }
                    } else {
                        _message.value = "Failed: This card was sent to ${giftCard.recipientEmail}, not you."
                    }
                } else {
                    _message.value = "Please log in to redeem."
                }
            } else {
                _message.value = "Invalid gift card code."
            }
        }
    }
}
