package com.life4.feedz.features.source.podcastSource

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.life4.core.core.view.BaseFragment
import com.life4.core.extensions.observe
import com.life4.feedz.HomeNavigationDirections
import com.life4.feedz.R
import com.life4.feedz.databinding.FragmentPodcastSourcesBinding
import com.life4.feedz.features.source.podcastSource.adapter.PodcastSourceAdapter
import com.life4.feedz.models.podcast.Feed
import com.life4.feedz.models.podcast.PodcastResponse
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PodcastSourceFragment :
    BaseFragment<FragmentPodcastSourcesBinding, PodcastSourceViewModel>(R.layout.fragment_podcast_sources) {
    private val viewModel: PodcastSourceViewModel by viewModels()
    private val podcastAdapter by lazy { PodcastSourceAdapter(::podcastClickListener) }

    override fun setupData() {
        super.setupData()
        setupViewModel(viewModel)
        observe(viewModel.state, ::onStateChanged)
        getBinding().rvPodcasts.adapter = podcastAdapter
    }

    override fun setupListener() {
        viewModel.getPodcastFeed()
    }

    private fun onStateChanged(state: PodcastSourceViewModel.State) {
        when (state) {
            is PodcastSourceViewModel.State.OnPodcastSuccess -> setPodcastAdapter(state.source)
        }
    }

    private fun setPodcastAdapter(data: PodcastResponse) {
        podcastAdapter.submitList(data.feeds?.distinct())
    }

    private fun podcastClickListener(item: Feed) {
        findNavController().navigate(
            HomeNavigationDirections.actionGlobalPodcastFragment(
                item.url ?: ""
            )
        )
    }


}