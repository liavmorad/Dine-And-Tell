package com.example.dine_and_tell.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Place(
    val name: String,
    val address: String,
    val lat: Double,
    val lon: Double,
    val imageUrl: String?,
    val categories: List<String>?,
    val cuisine: String?
) : Parcelable