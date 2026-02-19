package com.example.dine_and_tell.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dine_and_tell.dao.ExperienceDao
import com.example.dine_and_tell.model.Experience
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ExperienceRepository(private val experienceDao: ExperienceDao) {
    private val db = FirebaseFirestore.getInstance()
    private val experiencesCollection = db.collection("experiences")

    private val _userExperiences = MutableLiveData<List<Experience>>()
    val userExperiences: LiveData<List<Experience>> = _userExperiences

    private val _restaurantExperiences = MutableLiveData<List<Experience>>()
    val restaurantExperiences: LiveData<List<Experience>> = _restaurantExperiences

    companion object {
        @Volatile
        private var instance: ExperienceRepository? = null

        fun getInstance(experienceDao: ExperienceDao) =
            instance ?: synchronized(this) {
                instance ?: ExperienceRepository(experienceDao).also { instance = it }
            }
    }

    suspend fun addExperience(experience: Experience): String {
        return withContext(Dispatchers.IO) {
            val documentReference = experiencesCollection.add(experience).await()
            val firestoreId = documentReference.id
            val experienceWithId = experience.copy(firestoreId = firestoreId)
            experienceDao.insert(experienceWithId)
            firestoreId
        }
    }

    suspend fun getExperiencesByUserId(userId: String) {
        withContext(Dispatchers.IO) {
            try {
                // Try to get from Room first for immediate results (offline support)
                val localExperiences = experienceDao.getByUserId(userId)
                withContext(Dispatchers.Main) {
                    _userExperiences.value = localExperiences
                }

                // Sync with Firestore
                val snapshot = experiencesCollection
                    .whereEqualTo("userId", userId)
                    .orderBy("dateOfVisit", Query.Direction.DESCENDING)
                    .get()
                    .await()
                val experiences = snapshot.documents.mapNotNull { document ->
                    document.toObject<Experience>()?.copy(firestoreId = document.id)
                }

                // Update local Room database
                experiences.forEach { experienceDao.insert(it) }

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
                // Try to get from Room first
                val localExperiences = experienceDao.getByRestaurantId(restaurantId)
                withContext(Dispatchers.Main) {
                    _restaurantExperiences.value = localExperiences
                }

                // Sync with Firestore
                val snapshot = experiencesCollection
                    .whereEqualTo("restaurantId", restaurantId)
                    .orderBy("dateOfVisit", Query.Direction.DESCENDING)
                    .get()
                    .await()
                val experiences = snapshot.documents.mapNotNull { document ->
                    document.toObject<Experience>()?.copy(firestoreId = document.id)
                }

                // Update local Room database
                experiences.forEach { experienceDao.insert(it) }

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
                experienceDao.update(experience)
            }
        }
    }

    suspend fun deleteExperience(firestoreId: String) {
        withContext(Dispatchers.IO) {
            experiencesCollection.document(firestoreId).delete().await()
            experienceDao.deleteByFirestoreId(firestoreId)
        }
    }
    
    fun clear() {
        _userExperiences.value = emptyList()
    }
}
