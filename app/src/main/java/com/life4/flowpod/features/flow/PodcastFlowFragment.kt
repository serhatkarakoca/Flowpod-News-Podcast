package com.life4.flowpod.features.flow

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.life4.core.core.view.BaseFragment
import com.life4.flowpod.HomeNavigationDirections
import com.life4.flowpod.R
import com.life4.flowpod.databinding.FragmentPodcastFlowBinding
import com.life4.flowpod.features.flow.adapter.PodcastFlowAdapter
import com.life4.flowpod.models.rss_.RssResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PodcastFlowFragment :
    BaseFragment<FragmentPodcastFlowBinding, PodcastFlowViewModel>(R.layout.fragment_podcast_flow) {
    private val viewModel: PodcastFlowViewModel by viewModels()
    private val podcastAdapter by lazy { PodcastFlowAdapter(::podcastClickListener) }

    private var job: Job? = null

    override fun setupDefinition(savedInstanceState: Bundle?) {
        setupViewModel(viewModel)
        getAllFlowPodcasts()
    }

    override fun setupListener() {
        getBinding().rvPodcasts.adapter = podcastAdapter

        getBinding().buttonExplorePodcasts.setOnClickListener {
            findNavController().navigate(HomeNavigationDirections.actionGlobalPodcastSourceFragment())
        }
    }

    private fun podcastClickListener(item: RssResponse) {
        findNavController().navigate(
            HomeNavigationDirections.actionGlobalPodcastFragment(
                item.feedUrl ?: ""
            )
        )
    }

    private fun getAllFlowPodcasts() {
        job?.cancel()
        job = viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getAllFlowPodcasts().collectLatest { it ->
                viewModel.flowPodcasts.postValue(it)
                it.mapNotNull { flowList -> flowList.podcastItem }
                    .let {
                        podcastAdapter.submitList(it)
                        getBinding().emptyLayout.isVisible = it.isEmpty()
                    }

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job = null
    }
}