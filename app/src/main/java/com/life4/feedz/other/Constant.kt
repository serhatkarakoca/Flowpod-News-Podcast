package com.life4.feedz.other

object Constant {
    const val APP_DB = "app.db"
    const val TIMEOUT = 60L
    const val START_PAGE_INDEX = 1L
    const val NETWORK_PAGE_SIZE = 30
    const val DELAY = 2000L

    val END_PREFIX: List<String> = listOf(
        "/rss",
        "/rss.xml",
        "/feed",
        ".rss"
    )

}