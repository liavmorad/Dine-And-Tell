package com.example.dine_and_tell.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dine_and_tell.dao.ExperienceDao
import com.example.dine_and_tell.model.Experience
import com.example.dine_and_tell.model.User
import com.example.dine_and_tell.model.Restaurant

@Database(entities = [Experience::class, User::class, Restaurant::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun experienceDao(): ExperienceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dine_and_tell_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
