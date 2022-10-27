package com.life4.feedz.other

object Constant {
    const val APP_DB = "app.db"
    const val TIMEOUT = 60L
    const val START_PAGE_INDEX = 1L
    const val NETWORK_PAGE_SIZE = 30
    const val DELAY = 2000L

    const val BREAKING_NEWS = 1
    const val TECH_NEWS = 2
    const val SPORT_NEWS = 3
    const val HEALTH_NEWS = 4
    const val FLOW = 5
    const val HOME_NEWS = 6

    const val PODCAST_COLLECTION = "podcast_collection"
    const val NOTIFICATION_CHANNEL_ID = "podcast"
    const val NOTIFICATION_ID = 1
    const val MEDIA_ROOT_ID = "root_id"
    const val NETWORK_ERROR = "NETWORK_ERROR"
    const val UPDATE_PLAYER_POSITION_INTERVAL = 100L
    const val START_MEDIA_PLAYBACK_ACTION = "START_MEDIA_PLAYBACK_ACTION"
    const val REFRESH_MEDIA_BROWSER_CHILDREN = "REFRESH_MEDIA_BROWSER_CHILDREN"

    val END_PREFIX: List<String> = listOf(
        "",
        "/rss",
        "/rss.xml",
        "/feed",
        ".rss"
    )

}