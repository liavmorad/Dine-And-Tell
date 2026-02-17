package com.example.dine_and_tell.firebase

import com.example.dine_and_tell.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

class FirebaseUserService {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    suspend fun saveUser(user: User) {
        usersCollection.document(user.id).set(user).await()
    }

    suspend fun getUser(userId: String): User? {
        val document = usersCollection.document(userId).get().await()
        return document.toObject<User>()
    }

    suspend fun updateUser(user: User) {
        usersCollection.document(user.id).set(user).await()
    }
}
