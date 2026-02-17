package com.example.dine_and_tell.model

import com.google.gson.annotations.SerializedName

data class Feature(
    @SerializedName("properties")
    val properties: Properties,
    @SerializedName("geometry")
    val geometry: Geometry
)
