package com.life4.feedz.features.saved.savedpodcast

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.life4.core.core.view.BaseFragment
import com.life4.feedz.HomeNavigationDirections
import com.life4.feedz.R
import com.life4.feedz.databinding.FragmentSavedPodcastBinding
import com.life4.feedz.features.main.MainViewModel
import com.life4.feedz.features.podcast.adapter.PodcastAdapter
import com.life4.feedz.models.rss_.RssPaginationItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SavedPodcastFragment :
    BaseFragment<FragmentSavedPodcastBinding, SavedPodcastViewModel>(R.layout.fragment_saved_podcast) {
    private val viewModel: SavedPodcastViewModel by viewModels()
    private val podcastAdapter by lazy { PodcastAdapter(::podcastClickListener) }
    private val mainViewModel: MainViewModel by activityViewModels()
    private var job: Job? = null

    override fun setupDefinition(savedInstanceState: Bundle?) {
        setupViewModel(viewModel)
        getDownloadedPodcasts()
    }

    override fun setupListener() {
        getBinding().rvPodcasts.adapter = podcastAdapter
    }

    private fun podcastClickListener(item: RssPaginationItem) {
        mainViewModel.playOrToggleSong(item)
        Log.d("PODCAST_CLICK", item.enclosure?.url.toString())
        findNavController().navigate(
            HomeNavigationDirections.actionGlobalPodcastDetailsFragment(
                item
            )
        )
    }

    private fun getDownloadedPodcasts() {
        job?.cancel()
        job = lifecycleScope.launch {
            viewModel.getAllSavedPodcasts().collectLatest { it ->
                it.map { it.podcastItem }.let { pd ->
                    podcastAdapter.submitList(pd)
                    pd.mapNotNull {
                        it
                    }.let {
                        mainViewModel.setMediaItems(it)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job = null
    }
}