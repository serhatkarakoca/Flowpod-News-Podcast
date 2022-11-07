package com.life4.flowpod.features.podcast

import android.os.Bundle
import android.text.Html
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.life4.core.core.view.BaseFragment
import com.life4.core.extensions.observe
import com.life4.flowpod.R
import com.life4.flowpod.databinding.FragmentPodcastBinding
import com.life4.flowpod.features.main.MainViewModel
import com.life4.flowpod.features.podcast.adapter.PodcastAdapter
import com.life4.flowpod.models.rss_.Itunes
import com.life4.flowpod.models.rss_.RssPaginationItem
import com.life4.flowpod.models.rss_.RssResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PodcastFragment :
    BaseFragment<FragmentPodcastBinding, PodcastViewModel>(R.layout.fragment_podcast) {
    private val viewModel: PodcastViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private var job: Job? = null
    private val podcastAdapter by lazy { PodcastAdapter(::podcastClickListener) }
    private val args: PodcastFragmentArgs by navArgs()
    private val itemDecorator by lazy {
        DividerItemDecoration(
            requireContext(),
            RecyclerView.VERTICAL
        )
    }

    override fun setupDefinition(savedInstanceState: Bundle?) {
        setupViewModel(viewModel)
        viewModel.getPodcastDetails(args.url)
        getSavedPodcasts()

        observe(viewModel.state, ::onStateChanged)
        ResourcesCompat.getDrawable(resources, R.drawable.divider, null)?.let {
            itemDecorator.setDrawable(it)
        }
        getBinding().rvPodcasts.addItemDecoration(itemDecorator)
        getBinding().rvPodcasts.adapter = podcastAdapter
    }

    override fun setupListener() {
        getBinding().imgAddToFlow.setOnClickListener {
            if (!viewModel.isSavedPodcast) {
                viewModel.podcastDetails.value?.copy(feedUrl = args.url)
                    ?.let { it1 -> viewModel.addPodcastToFlow(it1) }
            } else {
                viewModel.deletePodcastFromFlow()
            }
        }
    }

    private fun podcastClickListener(item: RssPaginationItem) {
        mainViewModel.playOrToggleSong(item)
        var copyItem: RssPaginationItem = item
        if (item.itunes?.image.isNullOrEmpty()) {
            copyItem = item.copy(
                itunes = Itunes(
                    image = viewModel.podcastDetails.value?.itunes?.image,
                    author = item.itunes?.author,
                    explicit = null,
                    owner = item.itunes?.owner,
                    summary = null,
                    duration = item.itunes?.duration
                )
            )
        }

        findNavController().navigate(
            PodcastFragmentDirections.actionGlobalPodcastDetailsFragment(
                copyItem
            )
        )
    }


    private fun getSavedPodcasts() {
        job?.cancel()
        job = viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getSavedPodcasts().collectLatest {
                viewModel.flowPodcasts.value = it
                viewModel.isSavedPodcast =
                    it.firstOrNull { it.podcastItem?.feedUrl == args.url } != null
                getBinding().isSavedPodcast = viewModel.isSavedPodcast
            }
        }
    }

    private fun onPodcastDetailsSuccess(data: RssResponse) {
        getBinding().item = data
        data.description?.let {
            getBinding().tvDescription.text =
                Html.fromHtml(data.description, Html.FROM_HTML_MODE_LEGACY)
        }
        val items = arrayListOf<RssPaginationItem>()
        data.items?.mapNotNull { it }?.let { mediaItems ->
            mediaItems.forEach {
                if (it.itunes?.image.isNullOrEmpty()) {
                    val copyItem = it.copy(
                        itunes = Itunes(
                            image = viewModel.podcastDetails.value?.itunes?.image,
                            author = viewModel.podcastDetails.value?.title,
                            explicit = null,
                            owner = viewModel.podcastDetails.value?.itunes?.owner,
                            summary = null,
                            duration = it.itunes?.duration
                        )
                    )
                    items.add(copyItem)
                } else if (it.itunes?.author == null) {
                    val copyItem = it.copy(
                        itunes = Itunes(
                            image = it.itunes?.image,
                            author = viewModel.podcastDetails.value?.title,
                            explicit = null,
                            owner = viewModel.podcastDetails.value?.itunes?.owner,
                            summary = null,
                            duration = it.itunes?.duration
                        )
                    )
                    items.add(copyItem)
                } else {
                    items.add(it)
                }
            }
            mainViewModel.setMediaItems(items)
            podcastAdapter.submitList(items)

        }
        activity?.let {
            it.title = data.title
        }
    }

    private fun onStateChanged(state: PodcastViewModel.State) {
        when (state) {
            is PodcastViewModel.State.OnPodcastDetails -> onPodcastDetailsSuccess(state.details)
        }
    }
}