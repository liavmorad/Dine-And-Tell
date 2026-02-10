package com.example.dine_and_tell.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dine_and_tell.model.Place
import com.example.dine_and_tell.repository.PlacesRepository
import kotlinx.coroutines.launch

class ExplorationViewModel(private val placesRepository: PlacesRepository) : ViewModel() {

    private val _places = MutableLiveData<ApiResult<List<Place>>>()
    val places: LiveData<ApiResult<List<Place>>> = _places

    fun getPlaces(filter: String, limit: Int) {
        viewModelScope.launch {
            _places.value = ApiResult.Loading
            try {
                val result = placesRepository.getPlaces(filter, limit)
                _places.value = ApiResult.Success(result)
            } catch (e: Exception) {
                _places.value = ApiResult.Error(e)
            }
        }
    }
}
