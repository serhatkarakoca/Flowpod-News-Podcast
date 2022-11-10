package com.life4.core.manager.theme

interface MyThemeManager {
    fun setDefaultTheme()
    fun getCurrentTheme(): Themes
    fun changeTheme(themeMode: Themes)
}
