package com.life4.core.features.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.life4.core.R
import com.life4.core.core.view.BaseAction
import com.life4.core.extensions.reset
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat(), BaseAction {
    private lateinit var languagePreference: ListPreference
    private lateinit var themePreference: ListPreference
    private lateinit var versionPreference: Preference
    private lateinit var privacyPreference: Preference
    private val viewModel: SettingsViewModel by viewModels()
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)
        setupDefinition(savedInstanceState)
        setupData()
        setupListener()
    }

    override fun setupDefinition(savedInstanceState: Bundle?) {
        languagePreference = findPreference(getString(R.string.KEY_LANGUAGE))!!
        themePreference = findPreference(getString(R.string.KEY_THEME))!!
        versionPreference = findPreference(getString(R.string.KEY_VERSION))!!
        privacyPreference = findPreference(getString(R.string.KEY_PRIVACY))!!
    }

    override fun setupData() {
        versionPreference.summary = viewModel.getVersionName()
        languagePreference.value = getString(viewModel.getLanguageResId())
        themePreference.value = getString(viewModel.getThemeResId())
    }

    override fun setupListener() {
        languagePreference.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                viewModel.changeLanguage(newValue = newValue.toString())
                requireActivity().reset()
                true
            }
        themePreference.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                viewModel.changeTheme(newValue = newValue.toString())
                true
            }
        privacyPreference.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                val link = getString(R.string.privacy_link)
                if (link.isNotEmpty())
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_link))))
                true
            }
    }
}
