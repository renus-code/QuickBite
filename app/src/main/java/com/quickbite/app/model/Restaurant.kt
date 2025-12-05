package com.quickbite.app.model

import com.google.gson.annotations.SerializedName

data class Restaurant(

    @SerializedName("Restaurant Address")
    val restaurantAddress: String?,

    @SerializedName("Restaurant Name")
    val restaurantName: String?,

    @SerializedName("Restaurant Phone")
    val restaurantPhone: String?,

    @SerializedName("Restaurant Price Range")
    val restaurantPriceRange: String?,

    @SerializedName("Restaurant Website")
    val restaurantWebsite: String?,

    @SerializedName("Restaurant Yelp URL")
    val restaurantYelpUrl: String?,

    @SerializedName("Restaurant Latitude")
    val restaurantLatitude: String?,

    @SerializedName("Restaurant Longitude")
    val restaurantLongitude: String?,

    @SerializedName("Category")
    val category: String?
)
