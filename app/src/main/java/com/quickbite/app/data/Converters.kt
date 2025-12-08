package com.quickbite.app.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.quickbite.app.model.Address

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromItems(items: List<String>?): String {
        return gson.toJson(items ?: emptyList<String>())
    }

    @TypeConverter
    fun toItems(json: String?): List<String> {
        if (json.isNullOrEmpty()) return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun fromAddressList(addresses: List<Address>?): String {
        return gson.toJson(addresses ?: emptyList<Address>())
    }

    @TypeConverter
    fun toAddressList(json: String?): List<Address> {
        if (json.isNullOrEmpty()) return emptyList()
        val type = object : TypeToken<List<Address>>() {}.type
        return gson.fromJson(json, type)
    }
}
