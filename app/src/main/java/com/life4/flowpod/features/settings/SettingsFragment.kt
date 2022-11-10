package com.life4.flowpod.features.settings

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.auth.FirebaseAuth
import com.life4.core.core.view.BaseFragment
import com.life4.core.extensions.move
import com.life4.flowpod.R
import com.life4.flowpod.data.MyPreference
import com.life4.flowpod.databinding.FragmentSettingsBinding
import com.life4.flowpod.features.login.LoginActivity
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
        getBinding().name = auth.currentUser?.displayName
        getBinding().browserCheck = pref.getBrowserInApp()

        getBinding().buttonSelectedSources.setOnClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToUserSourcesFragment())
        }

        getBinding().loginLogout.setOnClickListener {
            activity?.let {
                if (auth.currentUser != null) {
                    pref.setUsername(null)
                    auth.signOut()
                }
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

        getBinding().buttonReview.setOnClickListener {
            val manager = ReviewManagerFactory.create(requireContext())
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reviewInfo = task.result
                    val flow = manager.launchReviewFlow(requireActivity(), reviewInfo)
                    flow.addOnCompleteListener { _ ->
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.feedback),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    // error
                }
            }
        }
    }
}