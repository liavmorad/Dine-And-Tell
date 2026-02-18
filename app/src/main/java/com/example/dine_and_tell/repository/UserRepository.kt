package com.example.dine_and_tell.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dine_and_tell.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepository private constructor() {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(): UserRepository {
            return instance ?: synchronized(this) {
                instance ?: UserRepository().also { instance = it }
            }
        }
    }

    suspend fun fetchUser(userId: String): User? {
        if (_currentUser.value?.id == userId) {
            return _currentUser.value
        }

        return withContext(Dispatchers.IO) {
            try {
                val document = usersCollection.document(userId).get().await()
                val user = document.toObject(User::class.java)

                withContext(Dispatchers.Main) {
                    _currentUser.value = user
                }
                user
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun saveUser(user: User) {
        withContext(Dispatchers.IO) {
            usersCollection.document(user.id).set(user).await()
            withContext(Dispatchers.Main) {
                _currentUser.value = user
            }
        }
    }
    
    suspend fun updateUser(user: User) {
        saveUser(user) // Same logic for Firestore set/merge
    }

    fun clear() {
        _currentUser.value = null
    }
}
