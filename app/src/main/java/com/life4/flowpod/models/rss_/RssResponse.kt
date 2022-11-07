package com.life4.flowpod.models.rss_


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RssResponse(
    @SerializedName("description")
    val description: String?,
    @SerializedName("feedUrl")
    val feedUrl: String?,
    @SerializedName("image")
    val image: Image?,
    @SerializedName("items")
    val items: List<RssPaginationItem?>?,
    @SerializedName("language")
    val language: String?,
    @SerializedName("lastBuildDate")
    val lastBuildDate: String?,
    @SerializedName("link")
    val link: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("itunes")
    val itunes: Itunes?
) : Parcelable {
    fun getImageSite(): String? {
        if (image?.url?.startsWith("http") == true || image?.url?.startsWith("https") == true)
            return image.url
        else
            return "https://" + image?.url?.substringAfter("www.")
    }

    private fun getHomePageUrl(): String? {
        return link?.substringAfter("//")?.substringBefore("/")?.trim()
    }

    fun getSiteName(): String? {
        val siteUrl = getHomePageUrl()
        return siteUrl?.substringAfter("www.")?.substringBefore(".")
            ?.replaceFirstChar { it.uppercaseChar() }
    }
}