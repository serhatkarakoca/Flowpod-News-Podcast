package com.life4.core.initializer

import android.content.Context
import androidx.startup.Initializer
import com.life4.core.manager.language.MyLanguageManager
import javax.inject.Inject

class LanguageInitializer : Initializer<MyLanguageManager> {
    @Inject
    lateinit var myLanguageManager: MyLanguageManager
    override fun create(context: Context): MyLanguageManager {
        InitializerEntryPoint.resolve(context).inject(this)
        myLanguageManager.setDefaultLanguage()
        return myLanguageManager
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}
