package com.example.dine_and_tell.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dine_and_tell.model.Experience
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ExperienceRepository {
    private val db = FirebaseFirestore.getInstance()
    private val experiencesCollection = db.collection("experiences")

    private val _userExperiences = MutableLiveData<List<Experience>>()
    val userExperiences: LiveData<List<Experience>> = _userExperiences

    private val _restaurantExperiences = MutableLiveData<List<Experience>>()
    val restaurantExperiences: LiveData<List<Experience>> = _restaurantExperiences

    companion object {
        @Volatile
        private var instance: ExperienceRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: ExperienceRepository().also { instance = it }
            }
    }

    suspend fun addExperience(experience: Experience): String {
        return withContext(Dispatchers.IO) {
            val documentReference = experiencesCollection.add(experience).await()
            documentReference.id
        }
    }

    suspend fun getExperiencesByUserId(userId: String) {
        withContext(Dispatchers.IO) {
            try {
                val snapshot = experiencesCollection.whereEqualTo("userId", userId).get().await()
                val experiences = snapshot.documents.mapNotNull { document ->
                    document.toObject<Experience>()?.copy(firestoreId = document.id)
                }
                withContext(Dispatchers.Main) {
                    _userExperiences.value = experiences
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun getExperiencesByRestaurantId(restaurantId: String) {
        withContext(Dispatchers.IO) {
            try {
                val snapshot = experiencesCollection.whereEqualTo("restaurantId", restaurantId).get().await()
                val experiences = snapshot.documents.mapNotNull { document ->
                    document.toObject<Experience>()?.copy(firestoreId = document.id)
                }
                withContext(Dispatchers.Main) {
                    _restaurantExperiences.value = experiences
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun updateExperience(experience: Experience) {
        withContext(Dispatchers.IO) {
            experience.firestoreId?.let {
                experiencesCollection.document(it).set(experience).await()
            }
        }
    }

    suspend fun deleteExperience(firestoreId: String) {
        withContext(Dispatchers.IO) {
            experiencesCollection.document(firestoreId).delete().await()
        }
    }
    
    fun clear() {
        _userExperiences.value = emptyList()
    }
}
