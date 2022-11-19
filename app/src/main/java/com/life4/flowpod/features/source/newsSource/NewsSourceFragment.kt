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
import com.life4.flowpod.other.Constant
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewsSourceFragment :
    BaseFragment<FragmentNewsSourcesBinding, NewsSourceViewModel>(R.layout.fragment_news_sources) {
    private val viewModel: NewsSourceViewModel by viewModels()
    private val breakingAdapter by lazy { SourceAdapter(::addSourceToPreference, 6) }
    private val techAdapter by lazy { SourceAdapter(::addSourceToPreference, 6) }
    private val sportAdapter by lazy { SourceAdapter(::addSourceToPreference, 6) }
    private val healthAdapter by lazy { SourceAdapter(::addSourceToPreference, 6) }
    private val financeAdapter by lazy { SourceAdapter(::addSourceToPreference, 6) }
    private val entertainmentAdapter by lazy { SourceAdapter(::addSourceToPreference, 6) }
    private val gamingAdapter by lazy { SourceAdapter(::addSourceToPreference, 6) }


    override fun setupDefinition(savedInstanceState: Bundle?) {
        setupViewModel(viewModel)
    }

    override fun setupListener() {
        observe(viewModel.liveData, ::onStateChanged)
        getBinding().rvBreakingNews.adapter = breakingAdapter
        getBinding().rvTechNews.adapter = techAdapter
        getBinding().rvSportNews.adapter = sportAdapter
        getBinding().rvHealthNews.adapter = healthAdapter
        getBinding().rvFinanceNews.adapter = financeAdapter
        getBinding().rvEntertainmentNews.adapter = entertainmentAdapter
        getBinding().rvGameNews.adapter = gamingAdapter

        getBinding().tvShowBreakingNews.setOnClickListener {
            findNavController().navigate(
                HomeNavigationDirections.actionGlobalSourcesListFragment(
                    viewModel.siteDataList.value?.filter { it.categoryId == Constant.BREAKING_NEWS }
                        ?.toTypedArray() ?: arrayOf()
                )
            )
        }

        getBinding().tvShowTechNews.setOnClickListener {
            findNavController().navigate(
                HomeNavigationDirections.actionGlobalSourcesListFragment(
                    viewModel.siteDataList.value?.filter { it.categoryId == Constant.TECH_NEWS }
                        ?.toTypedArray() ?: arrayOf()
                )
            )
        }

        getBinding().tvShowSportNews.setOnClickListener {
            findNavController().navigate(
                HomeNavigationDirections.actionGlobalSourcesListFragment(
                    viewModel.siteDataList.value?.filter { it.categoryId == Constant.SPORT_NEWS }
                        ?.toTypedArray() ?: arrayOf()
                )
            )
        }

        getBinding().tvShowHealthNews.setOnClickListener {
            findNavController().navigate(
                HomeNavigationDirections.actionGlobalSourcesListFragment(
                    viewModel.siteDataList.value?.filter { it.categoryId == Constant.HEALTH_NEWS }
                        ?.toTypedArray() ?: arrayOf()
                )
            )
        }

        getBinding().tvShowEntertainmentNews.setOnClickListener {
            findNavController().navigate(
                HomeNavigationDirections.actionGlobalSourcesListFragment(
                    viewModel.siteDataList.value?.filter { it.categoryId == Constant.ENTERTAINMENT }
                        ?.toTypedArray() ?: arrayOf()
                )
            )
        }

        getBinding().tvShowFinanceNews.setOnClickListener {
            findNavController().navigate(
                HomeNavigationDirections.actionGlobalSourcesListFragment(
                    viewModel.siteDataList.value?.filter { it.categoryId == Constant.BUSINESS_FINANCE }
                        ?.toTypedArray() ?: arrayOf()
                )
            )
        }

        getBinding().tvShowGameNews.setOnClickListener {
            findNavController().navigate(
                HomeNavigationDirections.actionGlobalSourcesListFragment(
                    viewModel.siteDataList.value?.filter { it.categoryId == Constant.GAME }
                        ?.toTypedArray() ?: arrayOf()
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
            is NewsSourceViewModel.State.OnSourceReady -> sourcesReady(state.list)
            is NewsSourceViewModel.State.OnSourceAdded -> sourceChanged()
        }
    }

    private fun sourceChanged() {
        val snackbar = Snackbar.make(
            requireView(),
            getString(R.string.updated_news_source),
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

    private fun sourcesReady(map: List<RssFeedResponseItem>) {
        map.forEach {
            if (viewModel.sourcePref.indexOf(it) != -1)
                it.isSelected = true

        }
        breakingAdapter.submitList(map.filter { it.categoryId == Constant.BREAKING_NEWS })
        techAdapter.submitList(map.filter { it.categoryId == Constant.TECH_NEWS })
        sportAdapter.submitList(map.filter { it.categoryId == Constant.SPORT_NEWS })
        entertainmentAdapter.submitList(map.filter { it.categoryId == Constant.ENTERTAINMENT })
        healthAdapter.submitList(map.filter { it.categoryId == Constant.HEALTH_NEWS })
        financeAdapter.submitList(map.filter { it.categoryId == Constant.BUSINESS_FINANCE })
        gamingAdapter.submitList(map.filter { it.categoryId == Constant.GAME })
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
                viewModel.siteDataList.value?.let {
                    sourcesReady(it)
                    return@observeOnce
                }
                getViewModel().getNewsSource()
            }

        }
    }
}