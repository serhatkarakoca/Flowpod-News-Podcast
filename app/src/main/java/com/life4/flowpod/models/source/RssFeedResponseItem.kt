package com.life4.flowpod.models.source


import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
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
    @SerializedName("description")
    val description: String?,
    @Expose
    var isSelected: Boolean = false
) : Parcelable