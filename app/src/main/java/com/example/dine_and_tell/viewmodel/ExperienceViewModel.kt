package com.example.dine_and_tell.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dine_and_tell.firebase.FirebaseExperienceService
import com.example.dine_and_tell.model.Experience
import kotlinx.coroutines.launch

class ExperienceViewModel : ViewModel() {

    private val firebaseExperienceService = FirebaseExperienceService()

    private val _experiences = MutableLiveData<List<Experience>>()
    val experiences: LiveData<List<Experience>> = _experiences

    private val _addExperienceStatus = MutableLiveData<Boolean>()
    val addExperienceStatus: LiveData<Boolean> = _addExperienceStatus

    fun addExperience(experience: Experience) {
        viewModelScope.launch {
            try {
                firebaseExperienceService.addExperience(experience)
                _addExperienceStatus.postValue(true)
            } catch (e: Exception) {
                _addExperienceStatus.postValue(false)
            }
        }
    }

    fun getExperiencesByUserId(userId: String) {
        viewModelScope.launch {
            try {
                _experiences.postValue(firebaseExperienceService.getExperiencesByUserId(userId))
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateExperience(experience: Experience) {
        viewModelScope.launch {
            try {
                firebaseExperienceService.updateExperience(experience)
                // Optionally, refresh the list of experiences
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteExperience(firestoreId: String) {
        viewModelScope.launch {
            try {
                firebaseExperienceService.deleteExperience(firestoreId)
                // Optionally, refresh the list of experiences
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}