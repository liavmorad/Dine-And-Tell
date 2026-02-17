package com.example.dine_and_tell.repository

import com.example.dine_and_tell.Constants
import com.example.dine_and_tell.model.Place
import com.example.dine_and_tell.network.RetrofitClient

class PlacesRepository : PlacesDataSource {
    override suspend fun getPlaces(filter: String, limit: Int): List<Place> {
        val featureCollection = RetrofitClient.instance.getPlaces(
            filter = filter,
            limit = limit,
            apiKey = Constants.GEOAPIFY_API_KEY
        )
        return featureCollection.features.mapNotNull { feature ->
            val properties = feature.properties
            val geometry = feature.geometry
            val id = properties.id
            val name = properties.name
            val address = properties.address
            val imageUrl = properties.datasource?.raw?.image
            val categories = properties.categories
            val cuisine = properties.catering?.cuisine
            val openingHours = properties.openingHours
            val phone = properties.datasource?.raw?.phone


            if (name != null && address != null) {
                Place(
                    id = id,
                    name = name,
                    address = address,
                    imageUrl = imageUrl,
                    categories = categories,
                    cuisine = cuisine,
                    openingHours = openingHours,
                    phone = phone
                )
            } else {
                null
            }
        }
    }
}
