package com.life4.feedz.features.settings

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.life4.core.core.view.BaseFragment
import com.life4.core.extensions.move
import com.life4.feedz.R
import com.life4.feedz.databinding.FragmentSettingsBinding
import com.life4.feedz.features.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment :
    BaseFragment<FragmentSettingsBinding, SettingsViewModel>(R.layout.fragment_settings) {
    private val viewModel: SettingsViewModel by viewModels()

    private lateinit var auth: FirebaseAuth

    override fun setupData() {
        setupViewModel(viewModel)
        auth = FirebaseAuth.getInstance()
    }

    override fun setupListener() {
        getBinding().isLogin = auth.currentUser != null
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
    }
}