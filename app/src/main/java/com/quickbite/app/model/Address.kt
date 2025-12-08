package com.quickbite.app.model

data class Address(
    val street: String = "",
    val city: String = "",
    val province: String = "",
    val postalCode: String = "",
) {
    fun toDisplayString(): String {
        val parts = listOf(street, city, province, postalCode).filter { it.isNotBlank() }
        return if (parts.isEmpty()) {
            "No address set"
        } else {
            parts.joinToString(", ")
        }
    }
}
