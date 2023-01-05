package com.example.core.data.network.response

import com.google.gson.annotations.SerializedName

data class DataWrapperResponse(
    @SerializedName("copyright")
    val copyRight: String,
    @SerializedName("data")
    val data: DataContainerResponse
)
