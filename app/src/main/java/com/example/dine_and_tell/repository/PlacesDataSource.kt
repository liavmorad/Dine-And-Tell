package com.example.dine_and_tell.repository

import com.example.dine_and_tell.model.Place

interface PlacesDataSource {
    suspend fun getPlaces(filter: String, limit: Int): List<Place>
}
