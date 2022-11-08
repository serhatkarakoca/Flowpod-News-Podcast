package com.life4.core.manager.language

import android.app.Application
import android.content.Context
import com.life4.core.R
import com.life4.core.manager.resource.MyResourceManager
import com.life4.core.manager.storage.MyStorageManager
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject

class MyLanguageManagerImp @Inject constructor(
    @ApplicationContext private val context: Context,
    private val myResourceManager: MyResourceManager,
    private val myStorageManager: MyStorageManager,
) : MyLanguageManager {
    override fun setDefaultLanguage() {
        changeLanguage(
            getCurrentLanguage(),
        )
    }

    override fun getCurrentLanguage(): Languages {
        val languageName = myStorageManager.getString(
            myResourceManager.getResources()
                .getString(R.string.KEY_LANGUAGE)
        )
        val lang = if (languageName.isEmpty()) {
            Languages.values()
                .firstOrNull { x ->
                    x.languageCode == Locale.getDefault()
                        .language
                }
        } else {
            Languages.values()
                .firstOrNull { x ->
                    myResourceManager.getResources()
                        .getString(x.languageName) == languageName
                }
        }
        return lang ?: Languages.ENGLISH
    }

    override fun changeLanguage(
        language: Languages,
    ) {
        try {
            Lingver.getInstance()
                .setLocale(
                    myResourceManager.getContext(),
                    Locale.forLanguageTag(language.languageCode)
                )
        } catch (e: IllegalStateException) {
            Lingver.init(
                context as Application,
                language.languageCode
            )
        }
        myStorageManager.setString(
            myResourceManager.getResources()
                .getString(R.string.KEY_LANGUAGE),
            myResourceManager.getResources()
                .getString(language.languageName)
        )
    }
}
