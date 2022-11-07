package com.life4.flowpod.features.source.newsSource

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.life4.core.core.view.BaseFragment
import com.life4.core.extensions.observe
import com.life4.core.extensions.observeOnce
import com.life4.flowpod.HomeNavigationDirections
import com.life4.flowpod.R
import com.life4.flowpod.databinding.FragmentNewsSourcesBinding
import com.life4.flowpod.extensions.dp
import com.life4.flowpod.features.source.adapter.SourceAdapter
import com.life4.flowpod.models.source.RssFeedResponseItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewsSourceFragment :
    BaseFragment<FragmentNewsSourcesBinding, NewsSourceViewModel>(R.layout.fragment_news_sources) {
    private val viewModel: NewsSourceViewModel by viewModels()
    private val breakingAdapter by lazy { SourceAdapter(::addSourceToPreference, 6) }
    private val techAdapter by lazy { SourceAdapter(::addSourceToPreference, 6) }
    private val sportAdapter by lazy { SourceAdapter(::addSourceToPreference, 6) }

    override fun setupDefinition(savedInstanceState: Bundle?) {
        setupViewModel(viewModel)
    }

    override fun setupListener() {
        observe(viewModel.liveData, ::onStateChanged)
        getBinding().rvBreakingNews.adapter = breakingAdapter
        getBinding().rvTechNews.adapter = techAdapter
        getBinding().rvSportNews.adapter = sportAdapter

        getBinding().tvShowBreakingNews.setOnClickListener {
            findNavController().navigate(
                HomeNavigationDirections.actionGlobalSourcesListFragment(
                    viewModel.siteDataListBreaking.value?.toTypedArray() ?: arrayOf()
                )
            )
        }

        getBinding().tvShowTechNews.setOnClickListener {
            findNavController().navigate(
                HomeNavigationDirections.actionGlobalSourcesListFragment(
                    viewModel.siteDataListTech.value?.toTypedArray() ?: arrayOf()
                )
            )
        }

        getBinding().tvShowSportNews.setOnClickListener {
            findNavController().navigate(
                HomeNavigationDirections.actionGlobalSourcesListFragment(
                    viewModel.siteDataListSports.value?.toTypedArray() ?: arrayOf()
                )
            )
        }

        getBinding().layoutSave.setOnClickListener {
            viewModel.getSavedSource().observeOnce(this, Observer {
                it?.let {
                    viewModel.deleteSavedSource()
                }
                viewModel.insertSourceToRoom()
            })
        }
    }

    private fun addSourceToPreference(item: RssFeedResponseItem, isChecked: Boolean) {
        if (isChecked) {
            item.isSelected = false
            viewModel.sourcePref.add(item)
        } else
            viewModel.sourcePref.remove(item)

        if (viewModel.sourcePref.isEmpty()
                .not() && getBinding().nestedScrollview.paddingBottom == 0
        ) {
            getBinding().nestedScrollview.setPadding(
                0,
                0,
                0,
                75.dp
            )
        } else if (viewModel.sourcePref.isEmpty()) {
            getBinding().nestedScrollview.setPadding(
                0,
                0,
                0,
                0
            )
        }
        getBinding().layoutSave.isVisible = viewModel.sourcePref.isEmpty().not()
    }

    private fun onStateChanged(state: NewsSourceViewModel.State) {
        when (state) {
            is NewsSourceViewModel.State.OnSourceReady -> sourcesReady(state.listMap)
            is NewsSourceViewModel.State.OnSourceAdded -> sourceChanged()
        }
    }

    private fun sourceChanged() {
        val snackbar = Snackbar.make(
            requireView(),
            "Haber Kaynaklarınız Güncellenmiştir.",
            Snackbar.ANIMATION_MODE_SLIDE
        )
        snackbar.show()
        getBinding().layoutSave.isVisible = false
        getBinding().nestedScrollview.setPadding(
            0,
            0,
            0,
            0
        )
    }

    private fun sourcesReady(map: MutableMap<Int, List<RssFeedResponseItem>?>) {
        map.forEach {
            it.value?.forEach {
                if (viewModel.sourcePref.indexOf(it) != -1)
                    it.isSelected = true
            }
        }
        breakingAdapter.submitList(map[1])
        techAdapter.submitList(map[2])
        sportAdapter.submitList(map[3])
        getBinding().nestedScrollview.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        activity?.let {
            it.title = getString(R.string.content_store)
        }
        getViewModel().getSavedSource().observeOnce(this) { sourceModel ->
            sourceModel?.sourceList?.let { source ->
                viewModel.sourcePref.clear()
                viewModel.sourcePref.addAll(source.sourceList)
            }.also {
                viewModel.allSources?.let {
                    sourcesReady(it)
                    return@observeOnce
                }
                getViewModel().getBreakingNewsSource()
            }

        }
    }
}