package com.quickbite.app.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.quickbite.app.model.Address
import com.quickbite.app.model.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("QuickBite_Settings", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_USER_EMAIL = "logged_in_user_email"
    }

    fun saveLoggedInUser(email: String?) {
        sharedPreferences.edit().putString(KEY_USER_EMAIL, email).apply()
    }

    fun getLoggedInUser(): String? {
        return sharedPreferences.getString(KEY_USER_EMAIL, null)
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
        sharedPreferences.edit().putString("menu_recent_searches", json).apply()
    }

    fun getRecentSearches(): List<String> {
        val json = sharedPreferences.getString("menu_recent_searches", null)
        return if (json != null) {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }
}
