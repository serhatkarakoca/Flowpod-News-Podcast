package com.life4.flowpod.models.rss_


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Owner(
    @SerializedName("email")
    val email: String?,
    @SerializedName("name")
    val name: String?
) : Parcelable