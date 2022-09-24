package com.life4.core.manager.language

interface MyLanguageManager {
    fun setDefaultLanguage()
    fun getCurrentLanguage(): Languages
    fun changeLanguage(language: Languages)
}
