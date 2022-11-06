package com.life4.feedz.features.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.life4.core.core.view.BaseActivity
import com.life4.core.extensions.observe
import com.life4.feedz.HomeNavigationDirections
import com.life4.feedz.R
import com.life4.feedz.databinding.ActivityMainBinding
import com.life4.feedz.exoplayer.service.isPlaying
import com.life4.feedz.exoplayer.toPodcast
import com.life4.feedz.models.rss_.RssPaginationItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity :
    BaseActivity<ActivityMainBinding, MainViewModel>(R.layout.activity_main) {
    private val viewModel: MainViewModel by viewModels()
    override fun onSupportNavigateUp(): Boolean =
        findNavController(R.id.navigation_host_fragment).navigateUp()

    private val currentPodcast = MutableLiveData<RssPaginationItem>()


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
                R.id.podcastDetailsFragment,
                R.id.podcastFragment,
                R.id.podcastListFragment,
                R.id.savedFragment,
                R.id.podcastSourceFragment,
                R.id.flowFragment -> toolbar.isVisible = false
                else -> toolbar.isVisible = true
            }
        }


        observe(viewModel.curPlayingSong) {
            val currentPodcast = it?.toPodcast()
            this.currentPodcast.value = currentPodcast
            if (viewModel.bottomPlayBackVisibility.value == true) {
                getBinding().podcast = currentPodcast
            }
        }

        observe(viewModel.bottomPlayBackVisibility) {
            if (it == false)
                getBinding().podcast = null
            else
                getBinding().podcast = currentPodcast.value
        }

        observe(viewModel.playbackState) {
            getBinding().imgPlay.setImageResource(
                if (it?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
            )
        }

        observe(viewModel.countDownTimer) {
            if (it == "0") {
                if (viewModel.playbackState.value?.isPlaying == true)
                    viewModel.togglePlaybackState()
            }
        }


        getBinding().podcastLayout.setOnClickListener {
            currentPodcast.value?.let {
                navController.navigate(
                    HomeNavigationDirections.actionGlobalPodcastDetailsFragment(
                        it
                    )
                )
            }
        }

        getBinding().imgPlay.setOnClickListener {
            viewModel.togglePlaybackState()
        }
    }
}
