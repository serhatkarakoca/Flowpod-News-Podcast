package com.life4.feedz.features.flow

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
import com.life4.feedz.R
import com.life4.feedz.databinding.BottomSheetFilterBinding
import com.life4.feedz.databinding.FragmentFlowBinding
import com.life4.feedz.features.flow.adapter.FilterAdapter
import com.life4.feedz.features.home.adapter.NewsAdapter
import com.life4.feedz.features.home.adapter.NewsLoadStateAdapter
import com.life4.feedz.models.rss_.RssPaginationItem
import com.life4.feedz.models.source.RssFeedResponseItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FlowFragment : BaseFragment<FragmentFlowBinding, FlowViewModel>(R.layout.fragment_flow) {
    private val viewModel: FlowViewModel by viewModels()
    private val newsAdapter by lazy { NewsAdapter(::newsClickListener) }
    private val filterAdapter by lazy { FilterAdapter(::filterListener) }
    private var pagingJob: Job? = null
    override fun setupDefinition(savedInstanceState: Bundle?) {
        setupViewModel(viewModel)
        getNews()
        observe(viewModel.liveData, ::onStateChanged)
        observe(viewModel.selectedCategory) {
            getAndSetNews()
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
                    getBinding().emptyLayout.isVisible =
                        viewModel.siteDataList.value?.isEmpty() == true
                }
        }
    }

    override fun setupListener() {
        getBinding().rvNews.adapter =
            newsAdapter.withLoadStateFooter(NewsLoadStateAdapter { newsAdapter.retry() })

        getBinding().toolbar.setOnClickListener { findNavController().popBackStack() }
        getBinding().refreshLayout.setOnRefreshListener {
            getBinding().refreshLayout.isRefreshing = false
        }

        getBinding().btnFilter.setOnClickListener {
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

        getBinding().categoryToggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.button_sport -> {
                        viewModel.fromFilter = false
                        viewModel.selectedCategory.value = 3
                    }
                    R.id.button_tech -> {
                        viewModel.fromFilter = false
                        viewModel.selectedCategory.value = 2
                    }
                    R.id.button_breaking -> {
                        viewModel.fromFilter = false
                        viewModel.selectedCategory.value = 1
                    }
                    R.id.button_all -> {
                        viewModel.fromFilter = false
                        viewModel.selectedCategory.value = 0
                    }
                }
            }

        }

    }

    private fun onStateChanged(state: FlowViewModel.State) {
        when (state) {
            is FlowViewModel.State.OnNewsFetch -> {}
        }
    }

    private fun newsClickListener(item: RssPaginationItem) {
        findNavController().navigate(
            FlowFragmentDirections.actionFlowFragmentToNewDetailsFragment(
                item
            )
        )
    }

    private fun getNews() {
        viewModel.getSources().observeOnce(this) {
            it?.let { source ->
                viewModel.userSources.value = source
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
}