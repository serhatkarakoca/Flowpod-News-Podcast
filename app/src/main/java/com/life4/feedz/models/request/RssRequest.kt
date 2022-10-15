package com.life4.feedz.models.request


import com.google.gson.annotations.SerializedName

data class RssRequest(
    @SerializedName("sites")
    val sites: List<String?>?
)