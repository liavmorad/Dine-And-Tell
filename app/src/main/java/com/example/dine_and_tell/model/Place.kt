package com.example.dine_and_tell.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Place(
    val id: String,
    val name: String,
    val address: String,
    val imageUrl: String?,
    val categories: List<String>?,
    val cuisine: String?,
    val phone: String?,
    val openingHours: String?
) : Parcelable