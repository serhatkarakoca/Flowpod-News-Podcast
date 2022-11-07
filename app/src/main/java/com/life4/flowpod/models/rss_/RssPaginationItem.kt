package com.life4.flowpod.models.rss_


import android.os.Parcelable
import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
@Entity(tableName = "news")
data class RssPaginationItem(
    @SerializedName("author")
    val author: String?,
    @SerializedName("comments")
    val comments: String?,
    @SerializedName("content")
    val content: String?,
    @SerializedName("contentSnippet")
    val contentSnippet: String?,
    @SerializedName("content:encoded")
    val contentEncoded: String?,
    @SerializedName("creator")
    val creator: String?,
    @SerializedName("dc:creator")
    val dcCreator: String?,
    @SerializedName("guid")
    val guid: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("isoDate")
    val isoDate: String?,
    @SerializedName("link")
    val link: String?,
    @SerializedName("pubDate")
    val pubDate: String?,
    @SerializedName("title")
    @PrimaryKey(autoGenerate = false)
    val title: String,
    @SerializedName("imageLogo")
    val siteImage: String?,
    @SerializedName("enclosure")
    val enclosure: Enclosure?,
    @SerializedName("itunes")
    val itunes: Itunes?,
    @Expose
    var categoryId: Int?,
    var pKey: String? = "0",
    var isFavorite: Boolean = false,
    var isDownloaded: Boolean = false
) : Parcelable {
    fun getHtmlContent(): String? {
        val content = contentEncoded ?: content
        val html =
            content?.replace("\\/", "")?.replace("[", "")?.replace("]", "")?.replace("<!", "")
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
        return link?.substringAfter("://")?.substringBefore("/")?.trim()
    }

    fun getLogo(): String? {
        siteImage ?: return null
        return if (siteImage.startsWith("http") || siteImage.startsWith("https"))
            siteImage
        else
            "https://" + siteImage.substringAfter("www.")
    }

    fun getSiteName(): String? {
        val siteUrl = getHomePageUrl()
        return siteUrl?.substringAfter("www.")?.substringBefore(".")
            ?.replaceFirstChar { it.uppercaseChar() }
    }


    fun getDateOfContent(): String? {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        if (isoDate != null) {
            val date = format.parse(isoDate)
            val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            if (date != null)
                return formattedDate.format(date)
        }
        return null
    }
}