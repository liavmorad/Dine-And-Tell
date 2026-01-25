package com.example.dine_and_tell.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

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
    val restaurantId: String,
    val userId: String,
    val review: String,
    val imageUrl: String? = null,
    val dateOfVisit: Long // Store as timestamp
)
