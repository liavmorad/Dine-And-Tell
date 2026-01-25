package com.example.dine_and_tell.firebase

import com.example.dine_and_tell.model.Experience
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

class FirebaseExperienceService {

    private val db = FirebaseFirestore.getInstance()
    private val experiencesCollection = db.collection("experiences")

    // Create
    suspend fun addExperience(experience: Experience): String {
        val documentReference = experiencesCollection.add(experience).await()
        return documentReference.id
    }

    // Read
    suspend fun getExperiencesByUserId(userId: String): List<Experience> {
        val snapshot = experiencesCollection.whereEqualTo("userId", userId).get().await()
        return snapshot.documents.mapNotNull { document ->
            document.toObject<Experience>()?.copy(firestoreId = document.id)
        }
    }

    // Update
    suspend fun updateExperience(experience: Experience) {
        experience.firestoreId?.let {
            experiencesCollection.document(it).set(experience).await()
        }
    }

    // Delete
    suspend fun deleteExperience(firestoreId: String) {
        experiencesCollection.document(firestoreId).delete().await()
    }
}