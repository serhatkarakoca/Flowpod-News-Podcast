package com.life4.feedz.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Author(
    @SerializedName("name")
    val name: String?
) : Parcelable