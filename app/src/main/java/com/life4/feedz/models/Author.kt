package com.life4.feedz.models


import com.google.gson.annotations.SerializedName

data class Author(
    @SerializedName("name")
    val name: String?
)