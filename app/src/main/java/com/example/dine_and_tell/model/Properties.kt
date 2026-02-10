package com.example.dine_and_tell.model

import com.google.gson.annotations.SerializedName

data class Properties(
    @SerializedName("name")
    val name: String?,
    @SerializedName("formatted")
    val address: String?,
    @SerializedName("datasource")
    val datasource: DataSource?,
)
data class DataSource(
    @SerializedName("raw")
    val raw: Raw?,
)
data class Raw(
    @SerializedName("image")
    val image: String?,
)
