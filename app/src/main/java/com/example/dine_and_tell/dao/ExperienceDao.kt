package com.example.dine_and_tell.dao

import androidx.room.*
import com.example.dine_and_tell.model.Experience

@Dao
interface ExperienceDao {
    @Query("SELECT * FROM experiences")
    suspend fun getAll(): List<Experience>

    @Query("SELECT * FROM experiences WHERE userId = :userId")
    suspend fun getByUserId(userId: String): List<Experience>

    @Query("SELECT * FROM experiences WHERE restaurantId = :restaurantId")
    suspend fun getByRestaurantId(restaurantId: String): List<Experience>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(experience: Experience): Long

    @Update
    suspend fun update(experience: Experience)

    @Delete
    suspend fun delete(experience: Experience)

    @Query("DELETE FROM experiences WHERE firestoreId = :firestoreId")
    suspend fun deleteByFirestoreId(firestoreId: String)
}
