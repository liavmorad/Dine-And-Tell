package com.example.dine_and_tell.model

import com.google.gson.annotations.SerializedName

data class Properties(
    @SerializedName("name")
    val name: String?,
    @SerializedName("formatted")
    val address: String?,
    @SerializedName("datasource")
    val datasource: DataSource?,
    @SerializedName("categories")
    val categories: List<String>?,
    @SerializedName("catering")
    val catering: Catering?,
    @SerializedName("opening_hours")
    val openingHours: String?,
)

data class Catering(
    @SerializedName("cuisine")
    val cuisine: String?,
)

data class DataSource(
    @SerializedName("raw")
    val raw: Raw?,
)
data class Raw(
    @SerializedName("image")
    val image: String?,
    @SerializedName("phone")
    val phone: String?,
)
