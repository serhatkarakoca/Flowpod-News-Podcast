package com.life4.feedz.features.podcast

import android.text.Html
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.life4.core.core.view.BaseFragment
import com.life4.core.extensions.observe
import com.life4.feedz.R
import com.life4.feedz.databinding.FragmentPodcastBinding
import com.life4.feedz.features.podcast.adapter.PodcastAdapter
import com.life4.feedz.models.rss_.RssPaginationItem
import com.life4.feedz.models.rss_.RssResponse
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PodcastFragment :
    BaseFragment<FragmentPodcastBinding, PodcastViewModel>(R.layout.fragment_podcast) {
    private val viewModel: PodcastViewModel by viewModels()
    private val podcastAdapter by lazy { PodcastAdapter(::podcastClickListener) }
    private val args: PodcastFragmentArgs by navArgs()
    private val itemDecorator by lazy {
        DividerItemDecoration(
            requireContext(),
            RecyclerView.VERTICAL
        )
    }

    override fun setupData() {
        super.setupData()
        setupViewModel(viewModel)
        observe(viewModel.state, ::onStateChanged)

        ResourcesCompat.getDrawable(resources, R.drawable.divider, null)?.let {
            itemDecorator.setDrawable(it)
        }
        getBinding().rvPodcasts.addItemDecoration(itemDecorator)
        getBinding().rvPodcasts.adapter = podcastAdapter
    }

    override fun setupListener() {
        viewModel.getPodcastDetails(args.url)
    }

    private fun podcastClickListener(item: RssPaginationItem) {

    }

    private fun onPodcastDetailsSuccess(data: RssResponse) {
        getBinding().item = data
        data.description?.let {
            getBinding().tvDescription.text =
                Html.fromHtml(data.description, Html.FROM_HTML_MODE_LEGACY)
        }
        podcastAdapter.submitList(data.items)
        activity?.let {
            it.title = data.title
        }
    }

    private fun onStateChanged(state: PodcastViewModel.State) {
        when (state) {
            is PodcastViewModel.State.OnPodcastDetails -> onPodcastDetailsSuccess(state.details)
        }
    }

    override fun onResume() {
        super.onResume()
    }
}