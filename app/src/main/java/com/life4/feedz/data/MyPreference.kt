package com.life4.feedz.data

import android.content.Context
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyPreference @Inject constructor(@ApplicationContext context: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    private val username = "KEY_USERNAME"

    fun setUsername(username: String) {
        prefs.edit().putString(this.username, username).apply()
    }

    fun getUsername(): String? = prefs.getString(username, null)

}