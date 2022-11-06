package com.life4.feedz.features.saved.savedpodcast

import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
    private val podcastAdapter by lazy {
        PodcastAdapter(
            ::podcastClickListener,
            ::deleteSavedPodcast
        )
    }
    private val mainViewModel: MainViewModel by activityViewModels()
    private var job: Job? = null

    override fun setupDefinition(savedInstanceState: Bundle?) {
        setupViewModel(viewModel)
        getDownloadedPodcasts()
    }

    override fun setupListener() {
        getBinding().rvPodcasts.adapter = podcastAdapter
        getBinding().buttonGoPodcasts.setOnClickListener {
            Log.d("CLICK_LISTENER", "click")
            findNavController().navigate(HomeNavigationDirections.actionGlobalPodcastSourceFragment())
        }
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

    private fun deleteSavedPodcast(item: RssPaginationItem) {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(getString(R.string.warning))
            setMessage(getString(R.string.are_u_sure_delete))
            setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
            setPositiveButton(getString(R.string.remove)) { dialog, _ ->
                val files = requireContext().filesDir.listFiles()
                files?.let {
                    files.filter { it.canRead() && it.isFile && it.name.endsWith(".mp3") }
                        .firstOrNull {
                            it.absolutePath.contains(
                                item.enclosure?.url?.substringAfter("/media").toString()
                            )
                        }?.delete()
                }
                viewModel.deleteDownloadedPodcast(item)
                dialog.dismiss()
            }
        }.show()
    }

    private fun getDownloadedPodcasts() {
        job?.cancel()
        job = lifecycleScope.launch {
            viewModel.getAllSavedPodcasts().collectLatest { it ->
                viewModel.downloadedPodcasts.postValue(it)
                it.map { it.podcastItem }.let { pd ->
                    podcastAdapter.submitList(pd)
                    pd.mapNotNull {
                        it
                    }.let {
                        mainViewModel.setMediaItems(it)
                        getBinding().emptyLayout.isVisible = it.isEmpty()
                        getBinding().rvPodcasts.isVisible = it.isNotEmpty()
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