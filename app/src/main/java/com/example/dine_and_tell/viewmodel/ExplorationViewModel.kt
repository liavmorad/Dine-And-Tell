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

    private val _isLoadingMore = MutableLiveData<Boolean>()
    val isLoadingMore: LiveData<Boolean> = _isLoadingMore

    private var currentPlaces = mutableListOf<Place>()
    private var currentLimit = 20
    private val limitIncrement = 20

    fun getPlaces(filter: String) {
        viewModelScope.launch {
            _places.value = ApiResult.Loading
            _isLoadingMore.value = false
            try {
                currentLimit = 20
                val result = placesRepository.getPlaces(filter, currentLimit)
                currentPlaces.clear()
                currentPlaces.addAll(result)
                _places.value = ApiResult.Success(currentPlaces)
            } catch (e: Exception) {
                _places.value = ApiResult.Error(e)
            }
        }
    }

    fun loadMorePlaces(filter: String) {
        viewModelScope.launch {
            _isLoadingMore.value = true
            try {
                currentLimit += limitIncrement
                val result = placesRepository.getPlaces(filter, currentLimit)
                currentPlaces.clear()
                currentPlaces.addAll(result)
                _places.value = ApiResult.Success(currentPlaces)
            } catch (e: Exception) {
                // In case of error, post the old list
                _places.value = ApiResult.Success(currentPlaces)
            } finally {
                _isLoadingMore.value = false
            }
        }
    }
}
