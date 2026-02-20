package com.example.dine_and_tell.model

import androidx.room.Entity
import androidx.room.PrimaryKey

import com.google.firebase.firestore.DocumentId

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "experiences")
data class Experience(
    @PrimaryKey @DocumentId val firestoreId: String = "",
    val restaurantId: String = "",
    val restaurantName: String = "",
    val userId: String = "",
    val userName: String = "",
    val rating: Float = 0f,
    val review: String = "",
    val imageUrl: String? = null,
    val dateOfVisit: Long = 0L // Store as timestamp
) : Parcelable
