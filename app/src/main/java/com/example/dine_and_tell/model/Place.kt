package com.example.dine_and_tell.model

data class Place(
    val name: String,
    val address: String,
    val lat: Double,
    val lon: Double,
    val imageUrl: String?
)
