package com.example.dine_and_tell.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dine_and_tell.repository.PlacesRepository

class ExplorationViewModelFactory(private val placesRepository: PlacesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExplorationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExplorationViewModel(placesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
