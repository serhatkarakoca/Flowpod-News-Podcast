package com.life4.feedz.features.settings

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.life4.core.core.view.BaseFragment
import com.life4.core.extensions.move
import com.life4.feedz.R
import com.life4.feedz.data.MyPreference
import com.life4.feedz.databinding.FragmentSettingsBinding
import com.life4.feedz.features.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment :
    BaseFragment<FragmentSettingsBinding, SettingsViewModel>(R.layout.fragment_settings) {
    private val viewModel: SettingsViewModel by viewModels()

    private lateinit var auth: FirebaseAuth

    @Inject
    lateinit var pref: MyPreference

    override fun setupDefinition(savedInstanceState: Bundle?) {
        super.setupDefinition(savedInstanceState)
        setupViewModel(viewModel)
        auth = FirebaseAuth.getInstance()
    }

    override fun setupListener() {
        activity?.let { it.title = getString(R.string.title_settings) }
        getBinding().isLogin = auth.currentUser != null
        getBinding().email = auth.currentUser?.email
        getBinding().browserCheck = pref.getBrowserInApp()

        getBinding().loginLogout.setOnClickListener {
            activity?.let {
                if (auth.currentUser != null)
                    auth.signOut()
                it.move(LoginActivity::class.java)
            }
        }

        getBinding().appSettings.setOnClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsFragment2ToSettingsFragment())
        }

        getBinding().checkboxBrowser.setOnCheckedChangeListener { buttonView, isChecked ->
            pref.setBrowserInApp(isChecked)
        }

        getBinding().buttonGoToSources.setOnClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToSourceFragment())
        }
    }
}