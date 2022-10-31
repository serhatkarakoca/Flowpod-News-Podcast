package com.life4.feedz.models.podcast.categories


import com.google.gson.annotations.SerializedName

data class PodcastCategories(
    @SerializedName("count")
    val count: Int?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("feeds")
    val feeds: List<Category?>?,
    @SerializedName("status")
    val status: String?
)