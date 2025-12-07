
package com.quickbite.app.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.quickbite.app.model.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("QuickBite_Settings", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _isLoggedIn = MutableStateFlow(isLoggedIn())
    val isLoggedInFlow: StateFlow<Boolean> = _isLoggedIn

    fun setLoggedIn(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean("isLoggedIn", isLoggedIn).apply()
        _isLoggedIn.value = isLoggedIn
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    fun saveCart(cartItems: List<CartItem>) {
        val json = gson.toJson(cartItems)
        sharedPreferences.edit().putString("cart_items", json).apply()
    }

    fun getCart(): List<CartItem> {
        val json = sharedPreferences.getString("cart_items", null)
        return if (json != null) {
            val type = object : TypeToken<List<CartItem>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun saveRecentSearches(searches: List<String>) {
        val json = gson.toJson(searches)
        sharedPreferences.edit().putString("recent_searches", json).apply()
    }

    fun getRecentSearches(): List<String> {
        val json = sharedPreferences.getString("recent_searches", null)
        return if (json != null) {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }
}
