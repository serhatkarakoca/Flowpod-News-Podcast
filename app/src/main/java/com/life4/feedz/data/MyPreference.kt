package com.life4.feedz.data

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyPreference @Inject constructor(@ApplicationContext context: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    private val username = "KEY_USERNAME"
    private val browserInApp = "KEY_BROWSER_IN_APP"
    private val preferences_time = "preferences_time"

    fun setUsername(username: String) {
        prefs.edit().putString(this.username, username).apply()
    }

    fun getUsername(): String? = prefs.getString(username, null)

    fun setBrowserInApp(value: Boolean) {
        prefs.edit().putBoolean(browserInApp, value).apply()
    }

    fun getBrowserInApp(): Boolean = prefs.getBoolean(browserInApp, true)

    fun saveTime(time: Long) {
        prefs.edit(commit = true) {
            putLong(preferences_time, time)
        }
    }

    fun getTime() = prefs.getLong(preferences_time, 0L)
}