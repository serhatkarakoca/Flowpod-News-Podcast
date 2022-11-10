package com.life4.flowpod.models.source

import com.google.gson.annotations.SerializedName


data class RssFeedResponse(
    @SerializedName("sources")
    val sourceList: List<RssFeedResponseItem>
)