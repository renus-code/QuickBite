package com.quickbite.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickbite.app.data.GiftCardRepository
import com.quickbite.app.data.UserRepository
import com.quickbite.app.model.Address
import com.quickbite.app.model.GiftCard
import com.quickbite.app.model.User
import com.quickbite.app.util.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class UserViewModel(
    private val repository: UserRepository,
    private val giftCardRepository: GiftCardRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _currentUser

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _darkModeEnabled = MutableStateFlow(false)
    val darkModeEnabled: StateFlow<Boolean> = _darkModeEnabled

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    init {
        val loggedInUserEmail = settingsManager.getLoggedInUser()
        if (loggedInUserEmail != null) {
            viewModelScope.launch {
                val user = repository.getUserByEmail(loggedInUserEmail)
                if (user != null) {
                    _currentUser.value = user
                    _isLoggedIn.value = true
                    _darkModeEnabled.value = user.isDarkMode
                }
            }
        }
    }

    fun setDarkMode(enabled: Boolean) {
        _darkModeEnabled.value = enabled
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
        if (repository.getUserByEmail(email) == null) {
            val newUser = User(email, password, displayName)
            repository.insertUser(newUser)
            return true
        }
        return false
    }

    suspend fun login(email: String, password: String): Boolean {
        val user = repository.getUserByEmail(email)
        if (user != null && user.password == password) {
            _currentUser.value = user
            _isLoggedIn.value = true
            _darkModeEnabled.value = user.isDarkMode
            settingsManager.saveLoggedInUser(email)
            return true
        }
        return false
    }

    fun logout() {
        _currentUser.value = null
        _isLoggedIn.value = false
        settingsManager.saveLoggedInUser(null)
    }

    fun updateUserProfile(
        displayName: String? = null,
        phoneNumber: String? = null,
        addresses: List<Address>? = null,
        avatarId: String? = null
    ) {
        viewModelScope.launch {
            _currentUser.value?.let {
                val updatedUser = it.copy(
                    displayName = displayName ?: it.displayName,
                    phoneNumber = phoneNumber ?: it.phoneNumber,
                    addresses = addresses ?: it.addresses,
                    avatarId = avatarId ?: it.avatarId
                )
                repository.updateUser(updatedUser)
                _currentUser.value = updatedUser
                _message.value = "Profile Updated Successfully"
            }
        }
    }

    fun addAddress(address: Address) {
        viewModelScope.launch {
            _currentUser.value?.let {
                val updatedAddresses = it.addresses + address
                updateUserProfile(addresses = updatedAddresses)
            }
        }
    }

    fun purchaseGiftCard(
        amount: Double,
        senderName: String,
        recipientName: String,
        recipientEmail: String,
        customCode: String? = null
    ) {
        viewModelScope.launch {
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
                    if (giftCard.recipientEmail.equals(currentUser.email, ignoreCase = true)) {
                        if (!giftCard.isRedeemed) {
                            val updatedGiftCard = giftCard.copy(
                                isRedeemed = true,
                                redeemedByUserId = currentUser.id
                            )
                            giftCardRepository.updateGiftCard(updatedGiftCard)
                            
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

    fun deleteAccount() {
        viewModelScope.launch {
            _currentUser.value?.let {
                repository.deleteUser(it)
                logout()
                _message.value = "Account Deleted"
            }
        }
    }
}
