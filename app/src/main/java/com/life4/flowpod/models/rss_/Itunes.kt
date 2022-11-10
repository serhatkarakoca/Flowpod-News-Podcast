package com.life4.flowpod.models.rss_


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Itunes(
    @SerializedName("author")
    val author: String?,
    @SerializedName("explicit")
    val explicit: String?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("owner")
    val owner: Owner?,
    @SerializedName("summary")
    val summary: String?,
    @SerializedName("duration")
    val duration: String?
) : Parcelable