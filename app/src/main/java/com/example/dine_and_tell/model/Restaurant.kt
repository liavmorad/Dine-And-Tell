package com.example.dine_and_tell.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
data class Restaurant(
    @PrimaryKey val id: String,
    val name: String,
    val address: String,
    val cuisine: String,
)