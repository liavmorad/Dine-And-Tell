package com.example.dine_and_tell.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dine_and_tell.model.Experience
import com.example.dine_and_tell.repository.ExperienceRepository
import kotlinx.coroutines.launch

class ExperienceViewModel(repository1: ExperienceRepository) : ViewModel() {

    private val repository = ExperienceRepository.getInstance()

    val experiences: LiveData<List<Experience>> = repository.userExperiences
    val restaurantExperiences: LiveData<List<Experience>> = repository.restaurantExperiences

    private val _addExperienceStatus = MutableLiveData<Boolean>()
    val addExperienceStatus: LiveData<Boolean> = _addExperienceStatus

    fun addExperience(experience: Experience) {
        viewModelScope.launch {
            try {
                repository.addExperience(experience)
                _addExperienceStatus.postValue(true)
            } catch (e: Exception) {
                _addExperienceStatus.postValue(false)
            }
        }
    }

    fun getExperiencesByUserId(userId: String) {
        viewModelScope.launch {
            repository.getExperiencesByUserId(userId)
        }
    }

    fun getExperiencesByRestaurantId(restaurantId: String) {
        viewModelScope.launch {
            repository.getExperiencesByRestaurantId(restaurantId)
        }
    }

    fun updateExperience(experience: Experience) {
        viewModelScope.launch {
            try {
                repository.updateExperience(experience)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteExperience(firestoreId: String) {
        viewModelScope.launch {
            try {
                repository.deleteExperience(firestoreId)
            } catch (e: Exception) {
                // @TODO Handle error
            }
        }
    }
}
