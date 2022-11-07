package com.life4.flowpod.features.search

import android.content.Context
import android.text.Html
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.life4.core.core.view.BaseFragment
import com.life4.core.extensions.observe
import com.life4.flowpod.R
import com.life4.flowpod.databinding.FragmentSearchBinding
import com.life4.flowpod.features.search.adapter.PodcastResultAdapter
import com.life4.flowpod.models.podcast.Feed
import com.life4.flowpod.models.podcast.PodcastResponse
import com.life4.flowpod.models.rss_.RssResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment :
    BaseFragment<FragmentSearchBinding, SearchViewModel>(R.layout.fragment_search) {
    private val viewModel: SearchViewModel by viewModels()
    private val args: SearchFragmentArgs by navArgs()
    private val podcastAdapter by lazy { PodcastResultAdapter(::podcastClickListener) }
    private val itemDecorator by lazy {
        DividerItemDecoration(
            requireContext(),
            RecyclerView.VERTICAL
        )
    }
    private var job: Job? = null

    private fun onStateChanged(state: SearchViewModel.State) {
        when (state) {
            is SearchViewModel.State.OnPodcastSearchSuccess -> setPodcastAdapter(state.podcastResult)
        }
    }

    private fun setPodcastAdapter(data: PodcastResponse) {
        podcastAdapter.submitList(data.feeds)
        getBinding().rvPodcasts.isVisible = true

    }

    private fun podcastClickListener(item: Feed) {
        item.url ?: return
        findNavController().navigate(
            SearchFragmentDirections.actionSearchFragmentToPodcastFragment(
                item.url
            )
        )
    }

    private fun setSiteData(data: RssResponse) {
        Log.d("siteData", data.toString())
        getBinding().rvPodcasts.isVisible = false
        getBinding().site = MutableLiveData(data)
        getBinding().isPodcast = data.itunes != null
        data.description?.let {
            getBinding().tvDescription.text =
                Html.fromHtml(data.description, Html.FROM_HTML_MODE_LEGACY)
        }
        getBinding().executePendingBindings()
    }

    override fun setupData() {
        super.setupData()
        setupViewModel(viewModel)
        observe(viewModel.siteData, ::setSiteData)
        observe(viewModel.state, ::onStateChanged)
        getBinding().progressStatus = viewModel.progressStatus

        ResourcesCompat.getDrawable(resources, R.drawable.divider, null)?.let {
            itemDecorator.setDrawable(it)
        }

        //getBinding().rvPodcasts.addItemDecoration(itemDecorator)
        getBinding().rvPodcasts.adapter = podcastAdapter
    }

    override fun setupListener() {

        getBinding().etSearch.addTextChangedListener {
            it?.let {
                job?.cancel()
                if (it.isNotEmpty())
                    job = viewLifecycleOwner.lifecycleScope.launch {
                        delay(1300)
                        if (args.isPodcast) {
                            viewModel.searchPodcast(it.toString())
                        } else {
                            viewModel.searchingBaseUrl = it.toString()
                            viewModel.tryToGetSite(it.toString())
                        }

                    }
            }
        }
        getBinding().cardView.setOnClickListener {
            val siteUrl = viewModel.confirmedUrl ?: return@setOnClickListener
            val siteDataItems = viewModel.siteData.value ?: return@setOnClickListener
            val isPodcast = viewModel.siteData.value?.itunes != null
            if (isPodcast) {
                findNavController().navigate(
                    SearchFragmentDirections.actionSearchFragmentToPodcastFragment(
                        siteUrl
                    )
                )
            } else {
                findNavController().navigate(
                    SearchFragmentDirections.actionSearchFragmentToNewsFragment(
                        siteUrl,
                        siteDataItems,
                        isPodcast
                    )
                )
            }
        }

    }

    private fun showKeyboard() {
        val inputMethodManager: InputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(getBinding().etSearch, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onResume() {
        super.onResume()
        getBinding().etSearch.requestFocus()
        showKeyboard()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job = null
        viewModel.indexUrlPath = 0
    }
}