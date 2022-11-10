package com.life4.core.features.settings

import com.life4.core.BuildConfig
import com.life4.core.core.vm.BaseViewModel
import com.life4.core.manager.language.Languages
import com.life4.core.manager.language.MyLanguageManager
import com.life4.core.manager.resource.MyResourceManager
import com.life4.core.manager.theme.MyThemeManager
import com.life4.core.manager.theme.Themes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val myLanguageManager: MyLanguageManager,
    private val myThemeManager: MyThemeManager,
    private val myResourceManager: MyResourceManager
) : BaseViewModel() {
    fun changeLanguage(newValue: String) = myLanguageManager.changeLanguage(
        Languages.values()
            .first { x ->
                myResourceManager.getResources()
                    .getString(x.languageName) == newValue
            }
    )

    fun changeTheme(newValue: String) = myThemeManager.changeTheme(
        Themes.values()
            .first { x ->
                myResourceManager.getResources()
                    .getString(x.themeName) == newValue
            }
    )

    fun getVersionName(): String = BuildConfig.VERSION_NAME
    fun getLanguageResId(): Int = myLanguageManager.getCurrentLanguage().languageName
    fun getThemeResId(): Int = myThemeManager.getCurrentTheme().themeName
}
