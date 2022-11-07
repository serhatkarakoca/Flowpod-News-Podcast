package com.life4.flowpod.models.podcast


import com.google.gson.annotations.SerializedName

data class Feed(
    @SerializedName("artwork")
    val artwork: String?,
    @SerializedName("author")
    val author: String?,
    @SerializedName("contentType")
    val contentType: String?,
    @SerializedName("crawlErrors")
    val crawlErrors: Int?,
    @SerializedName("dead")
    val dead: Int?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("episodeCount")
    val episodeCount: Int?,
    @SerializedName("explicit")
    val explicit: Boolean?,
    @SerializedName("generator")
    val generator: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("itunesId")
    val itunesId: Int?,
    @SerializedName("language")
    val language: String?,
    @SerializedName("lastCrawlTime")
    val lastCrawlTime: Int?,
    @SerializedName("lastGoodHttpStatusTime")
    val lastGoodHttpStatusTime: Int?,
    @SerializedName("lastHttpStatus")
    val lastHttpStatus: Int?,
    @SerializedName("lastParseTime")
    val lastParseTime: Int?,
    @SerializedName("lastUpdateTime")
    val lastUpdateTime: Int?,
    @SerializedName("link")
    val link: String?,
    @SerializedName("locked")
    val locked: Int?,
    @SerializedName("originalUrl")
    val originalUrl: String?,
    @SerializedName("ownerName")
    val ownerName: String?,
    @SerializedName("parseErrors")
    val parseErrors: Int?,
    @SerializedName("podcastGuid")
    val podcastGuid: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("type")
    val type: Int?,
    @SerializedName("url")
    val url: String?
)