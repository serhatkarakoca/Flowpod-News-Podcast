package com.life4.flowpod.other

object Constant {
    const val TIMEOUT = 30L

    // News categories
    const val BREAKING_NEWS = 1
    const val TECH_NEWS = 2
    const val SPORT_NEWS = 3
    const val HEALTH_NEWS = 4
    const val FLOW = 5
    const val HOME_NEWS = 6
    const val BUSINESS_FINANCE = 7
    const val ENTERTAINMENT = 8
    const val GAME = 9

    //Notification constants
    const val PODCAST_COLLECTION = "podcast_collection"
    const val NOTIFICATION_CHANNEL_ID = "podcast"
    const val NOTIFICATION_ID = 1
    const val APP_NOTIFICATION_CHANNEL_ID = "BREAKING_NEWS"
    const val APP_NOTIFICATION_ID = 3
    const val MEDIA_ROOT_ID = "root_id"
    const val NETWORK_ERROR = "NETWORK_ERROR"
    const val UPDATE_PLAYER_POSITION_INTERVAL = 100L
    const val START_MEDIA_PLAYBACK_ACTION = "START_MEDIA_PLAYBACK_ACTION"
    const val REFRESH_MEDIA_BROWSER_CHILDREN = "REFRESH_MEDIA_BROWSER_CHILDREN"
    const val DOWNLOAD_CHANNEL_NAME = "DOWNLOAD_CHANNEL_NAME"
    const val DOWNLOAD_NOTIFICATION_ID = 2

    val END_PREFIX: List<String> = listOf(
        "",
        "/rss",
        "/rss.xml",
        "/feed",
        ".rss"
    )

}