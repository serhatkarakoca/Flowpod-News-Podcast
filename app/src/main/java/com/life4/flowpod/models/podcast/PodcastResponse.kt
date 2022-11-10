package com.life4.flowpod.models.podcast


import com.google.gson.annotations.SerializedName

data class PodcastResponse(
    @SerializedName("count")
    val count: Int?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("feeds")
    val feeds: List<Feed?>?,
    @SerializedName("query")
    val query: String?,
    @SerializedName("status")
    val status: String?
)