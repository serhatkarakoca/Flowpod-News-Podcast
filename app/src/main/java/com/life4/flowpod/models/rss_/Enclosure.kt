package com.life4.flowpod.models.rss_


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Enclosure(
    @SerializedName("length")
    val length: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("url")
    val url: String?
) : Parcelable