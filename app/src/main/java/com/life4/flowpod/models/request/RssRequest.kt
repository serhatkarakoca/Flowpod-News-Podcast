package com.life4.flowpod.models.request


import com.google.gson.annotations.SerializedName

data class RssRequest(
    @SerializedName("sites")
    val sites: List<String?>?
)