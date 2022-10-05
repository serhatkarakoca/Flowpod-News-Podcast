package com.life4.feedz.models


import android.os.Parcelable
import android.util.Log
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
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
) : Parcelable {
    fun getHtmlContent(): String? {

        val html =
            contentHtml?.replace("\\/", "")?.replace("[", "")?.replace("]", "")?.replace("<!", "")
                ?.replace("CDATA", "")
        Log.d("htmlFrom", html.toString())
        return html
    }

    fun getPostImage(): String? {
        val html = getHtmlContent()
        Log.d(
            "htmlFromImage",
            html?.substringAfter("src=")?.substringBefore("alt")?.substringBefore("height")
                ?.substringBefore("href")?.substringBefore(" ", "")?.replace("\"", "")
                ?.replace("'", "")?.trim().toString()
        )
        return html?.substringAfter("src=")?.substringBefore("alt")?.substringBefore("height")
            ?.substringBefore("href")?.substringBefore(" ", "")?.replace("\"", "")?.replace("'", "")
            ?.trim()
    }

    fun getHomePageUrl(): String? {
        return url?.substringAfter("://")?.substringBefore("/")?.trim()
    }
}