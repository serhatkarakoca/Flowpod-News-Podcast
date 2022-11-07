package com.life4.feedz.features.source.podcastSource

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.life4.core.core.view.BaseFragment
import com.life4.core.extensions.observe
import com.life4.feedz.R
import com.life4.feedz.databinding.FragmentPodcastSourcesBinding
import com.life4.feedz.features.podcast.adapter.PodcastCategoryAdapter
import com.life4.feedz.models.podcast.categories.Category
import com.life4.feedz.models.podcast.categories.PodcastCategories
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PodcastSourceFragment :
    BaseFragment<FragmentPodcastSourcesBinding, PodcastSourceViewModel>(R.layout.fragment_podcast_sources) {
    private val viewModel: PodcastSourceViewModel by viewModels()
    private val podcastAdapter by lazy { PodcastCategoryAdapter(::podcastClickListener) }

    override fun setupDefinition(savedInstanceState: Bundle?) {
        setupViewModel(viewModel)
        observe(viewModel.state, ::onStateChanged)
        getBinding().rvPodcasts.adapter = podcastAdapter

    }

    override fun setupListener() {
        viewModel.getPodcastCategories()
    }

    private fun onStateChanged(state: PodcastSourceViewModel.State) {
        when (state) {
            is PodcastSourceViewModel.State.OnPodcastCategorySuccess -> setPodcastAdapter(state.source)
            else -> Unit
        }
    }


    private fun setPodcastAdapter(data: PodcastCategories) {
        val dataList =
            data.feeds?.filter { viewModel.categoryList.contains(it?.name?.lowercase()) }

        podcastAdapter.submitList(dataList)
    }

    private fun podcastClickListener(item: Category) {
        findNavController().navigate(
            PodcastSourceFragmentDirections.actionGlobalPodcastListFragment(
                item.name ?: ""
            )
        )
    }


}