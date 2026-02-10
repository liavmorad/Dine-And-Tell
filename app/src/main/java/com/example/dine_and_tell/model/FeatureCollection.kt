package com.example.dine_and_tell.model

import com.google.gson.annotations.SerializedName

data class FeatureCollection(
    @SerializedName("features")
    val features: List<Feature>
)
