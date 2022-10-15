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

    val END_PREFIX: List<String> = listOf(
        "/rss",
        "/rss.xml",
        "/feed",
        ".rss"
    )

}