package com.life4.feedz.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RssResponse(
    @SerializedName("description")
    val description: String?,
    @SerializedName("home_page_url")
    val homePageUrl: String?,
    @SerializedName("items")
    val items: List<Item?>?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("version")
    val version: String?
) : Parcelable