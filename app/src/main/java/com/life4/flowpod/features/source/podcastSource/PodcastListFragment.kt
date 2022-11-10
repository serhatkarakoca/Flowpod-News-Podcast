package com.life4.flowpod.features.source.podcastSource

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.life4.core.core.view.BaseFragment
import com.life4.core.extensions.observe
import com.life4.flowpod.R
import com.life4.flowpod.databinding.FragmentPodcastListBinding
import com.life4.flowpod.features.source.podcastSource.adapter.PodcastSourceAdapter
import com.life4.flowpod.models.podcast.Feed
import com.life4.flowpod.models.podcast.PodcastResponse
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PodcastListFragment :
    BaseFragment<FragmentPodcastListBinding, PodcastListViewModel>(R.layout.fragment_podcast_list) {
    private val viewModel: PodcastListViewModel by viewModels()
    private val podcastAdapter by lazy { PodcastSourceAdapter(::podcastClickListener) }
    private val args: PodcastListFragmentArgs by navArgs()

    override fun setupDefinition(savedInstanceState: Bundle?) {
        setupViewModel(viewModel)
        observe(viewModel.state, ::onStateChanged)
        getBinding().rvPodcasts.adapter = podcastAdapter
        getBinding().buttonGoNews.setOnClickListener { findNavController().popBackStack() }
    }

    override fun setupListener() {
        viewModel.getPodcastFeed(args.category)
    }

    private fun onStateChanged(state: PodcastListViewModel.State) {
        when (state) {
            is PodcastListViewModel.State.OnPodcastSuccess -> setPodcastAdapter(state.source)
        }
    }

    private fun setPodcastAdapter(data: PodcastResponse) {
        getBinding().emptyLayout.isVisible = data.feeds.isNullOrEmpty()
        podcastAdapter.submitList(data.feeds?.distinct())
    }

    private fun podcastClickListener(item: Feed) {
        findNavController().navigate(
            PodcastListFragmentDirections.actionGlobalPodcastFragment(
                item.url ?: ""
            )
        )
    }
}