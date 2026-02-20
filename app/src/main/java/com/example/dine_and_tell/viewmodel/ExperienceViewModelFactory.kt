package com.example.dine_and_tell.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dine_and_tell.repository.ExperienceRepository

class ExperienceViewModelFactory(private val repository: ExperienceRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExperienceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExperienceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
