package com.life4.flowpod.features.flow.newsflow

import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.life4.core.core.view.BaseFragment
import com.life4.core.extensions.observe
import com.life4.core.extensions.observeOnce
import com.life4.flowpod.HomeNavigationDirections
import com.life4.flowpod.R
import com.life4.flowpod.databinding.BottomSheetFilterBinding
import com.life4.flowpod.databinding.FragmentNewsFlowBinding
import com.life4.flowpod.features.flow.adapter.FilterAdapter
import com.life4.flowpod.features.home.adapter.NewsAdapter
import com.life4.flowpod.features.home.adapter.NewsLoadStateAdapter
import com.life4.flowpod.models.rss_.RssPaginationItem
import com.life4.flowpod.models.source.RssFeedResponseItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewsFlowFragment :
    BaseFragment<FragmentNewsFlowBinding, NewsFlowViewModel>(R.layout.fragment_news_flow) {
    private val viewModel: NewsFlowViewModel by viewModels()
    private val newsAdapter by lazy { NewsAdapter(::newsClickListener) }
    private val filterAdapter by lazy { FilterAdapter(::filterListener) }
    private var pagingJob: Job? = null
    override fun setupDefinition(savedInstanceState: Bundle?) {
        setupViewModel(viewModel)
        observe(viewModel.liveData, ::onStateChanged)
        observe(viewModel.selectedCategory) {
            if (viewModel.userSources.value?.sourceList?.sourceList.isNullOrEmpty().not())
                getAndSetNews()
            else
                getBinding().emptyLayout.isVisible = true
        }
    }

    private fun filterListener(item: RssFeedResponseItem, isChecked: Boolean) {
        item.isSelected = isChecked
    }

    override fun onStart() {
        super.onStart()
        initQuery()
    }

    private fun initQuery() {
        // Scroll to top when the list is refreshed from network.
        lifecycleScope.launch {
            newsAdapter.loadStateFlow
                // Only emit when REFRESH LoadState for RemoteMediator changes.
                .distinctUntilChangedBy { it.refresh }
                // Only react to cases where Remote REFRESH completes i.e., NotLoading.
                .filter { it.refresh is LoadState.NotLoading }
                .collectLatest {
                    //getBinding().rvNews.scrollToPosition(0)
                    getBinding().rvNews.isVisible =
                        viewModel.siteDataList.value.isNullOrEmpty().not()
                    getBinding().emptyLayout.isVisible =
                        viewModel.siteDataList.value?.isEmpty() == true
                }
        }
    }

    override fun setupListener() {
        getBinding().rvNews.adapter =
            newsAdapter.withLoadStateFooter(NewsLoadStateAdapter { newsAdapter.retry() })

        getBinding().buttonFilter.setOnClickListener {
            BottomSheetDialog(requireContext()).apply {
                val inflater = LayoutInflater.from(requireContext())
                val binding = DataBindingUtil.inflate<BottomSheetFilterBinding>(
                    inflater,
                    R.layout.bottom_sheet_filter,
                    null,
                    false
                )
                binding.rvSources.adapter = filterAdapter
                filterAdapter.submitList(viewModel.selectedUserList.value)
                binding.btnDone.setOnClickListener {
                    viewModel.fromFilter = true
                    getAndSetNews()
                    dismiss()
                }
                setContentView(binding.root)
            }.show()
        }

        getBinding().buttonGoNews.setOnClickListener {
            findNavController().navigate(HomeNavigationDirections.actionGlobalSourcesFragment())
        }

    }

    private fun onStateChanged(state: NewsFlowViewModel.State) {
        when (state) {
            is NewsFlowViewModel.State.OnNewsFetch -> {}
        }
    }

    private fun newsClickListener(item: RssPaginationItem) {
        findNavController().navigate(
            HomeNavigationDirections.actionGlobalNewsDetailsFragment(
                item
            )
        )
    }

    private fun getNews() {
        viewModel.getSources().observeOnce(this) {
            it?.let { source ->
                viewModel.userSources.value = source
                if (it.sourceList?.sourceList.isNullOrEmpty().not())
                    getAndSetNews()
            }
        }
    }

    private fun getAndSetNews() {
        pagingJob?.cancel()
        pagingJob = viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getFilteredNews().collectLatest {
                newsAdapter.submitData(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        pagingJob = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.userSources.value?.let {
            getAndSetNews()
        } ?: getNews()
    }
}