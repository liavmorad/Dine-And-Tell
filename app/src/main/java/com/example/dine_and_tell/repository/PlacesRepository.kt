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
            val name = properties.name
            val address = properties.address
            val lon = geometry.coordinates.getOrNull(0)
            val lat = geometry.coordinates.getOrNull(1)
            val imageUrl = properties.datasource?.raw?.image

            if (name != null && address != null && lon != null && lat != null) {
                Place(
                    name = name,
                    address = address,
                    lat = lat,
                    lon = lon,
                    imageUrl = imageUrl
                )
            } else {
                null
            }
        }
    }
}
