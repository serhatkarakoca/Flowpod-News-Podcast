package com.life4.core.manager.language

import com.life4.core.R

enum class Languages(
    val languageName: Int,
    val languageCode: String
) {
    ENGLISH(
        R.string.language_english,
        "en"
    ),
    TURKISH(
        R.string.language_turkish,
        "tr"
    )
}
