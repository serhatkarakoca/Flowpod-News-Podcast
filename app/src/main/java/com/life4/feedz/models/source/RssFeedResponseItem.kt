package com.life4.feedz.models.source


import com.google.gson.annotations.SerializedName

data class RssFeedResponseItem(
    @SerializedName("category")
    val category: String?,
    @SerializedName("category_id")
    val categoryId: Int?,
    @SerializedName("language")
    val language: String?,
    @SerializedName("site_logo")
    val siteLogo: String?,
    @SerializedName("site_name")
    val siteName: String?,
    @SerializedName("site_url")
    val siteUrl: String?,
    var isSelected: Boolean = false
)