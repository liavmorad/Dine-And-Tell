package com.example.dine_and_tell.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

import com.google.firebase.firestore.DocumentId

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "experiences",
    foreignKeys = [
        ForeignKey(
            entity = Restaurant::class,
            parentColumns = ["id"],
            childColumns = ["restaurantId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Experience(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @DocumentId val firestoreId: String? = null,
    val restaurantId: String = "",
    val restaurantName: String = "",
    val userId: String = "",
    val rating: Float = 0f,
    val review: String = "",
    val imageUrl: String? = null,
    val dateOfVisit: Long = 0L // Store as timestamp
) : Parcelable
