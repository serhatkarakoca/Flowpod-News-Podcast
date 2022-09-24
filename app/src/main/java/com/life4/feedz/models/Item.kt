package com.life4.feedz.models


import com.google.gson.annotations.SerializedName

data class Item(
    @SerializedName("author")
    val author: Author?,
    @SerializedName("content_html")
    val contentHtml: String?,
    @SerializedName("date_published")
    val datePublished: String?,
    @SerializedName("guid")
    val guid: String?,
    @SerializedName("summary")
    val summary: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("url")
    val url: String?
)