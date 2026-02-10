package com.example.dine_and_tell.network

import com.example.dine_and_tell.model.FeatureCollection
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesApiService {
    @GET("places")
    suspend fun getPlaces(
        @Query("categories") categories: String = "catering.restaurant",
        @Query("filter") filter: String,
        @Query("limit") limit: Int,
        @Query("apiKey") apiKey: String,
    ): FeatureCollection
}
