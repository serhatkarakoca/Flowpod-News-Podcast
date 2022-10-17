package com.life4.feedz.features.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.life4.core.core.view.BaseActivity
import com.life4.feedz.R
import com.life4.feedz.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity :
    BaseActivity<ActivityMainBinding, MainViewModel>(R.layout.activity_main) {
    private val viewModel: MainViewModel by viewModels()
    override fun onSupportNavigateUp(): Boolean =
        findNavController(R.id.navigation_host_fragment).navigateUp()

    override fun onBackPressed() {
        if (!onSupportNavigateUp())
            super.onBackPressed()
    }

    override fun beforeOnCreated() {
       installSplashScreen().setKeepOnScreenCondition { viewModel.cachedData }
    }

    override fun setupDefinition(savedInstanceState: Bundle?) {
        setupViewModel(viewModel)
        viewModel.getCachedData()
        val toolbar = getBinding().toolbar
        val bottomNavigationView = getBinding().bottomNavigationView
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navigation_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setSupportActionBar(toolbar)
        toolbar.setupWithNavController(navController, appBarConfiguration)
        bottomNavigationView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment,
                R.id.flowFragment -> toolbar.isVisible = false
                else -> toolbar.isVisible = true
            }
        }
    }
}
