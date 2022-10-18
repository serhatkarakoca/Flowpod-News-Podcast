package com.life4.feedz.features.saved

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.life4.core.core.view.BaseFragment
import com.life4.core.extensions.observe
import com.life4.feedz.R
import com.life4.feedz.databinding.FragmentSavedNewsBinding
import com.life4.feedz.models.rss_.RssPaginationItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SavedNewsFragment :
    BaseFragment<FragmentSavedNewsBinding, SavedNewsViewModel>(R.layout.fragment_saved_news) {
    private val viewModel: SavedNewsViewModel by viewModels()
    private val newsAdapter by lazy { CardNewsAdapter(::newsClickListener, ::favClickListener) }
    private var job: Job? = null

    override fun setupDefinition(savedInstanceState: Bundle?) {
        super.setupDefinition(savedInstanceState)
        setupViewModel(viewModel)
        observe(viewModel.state, ::onStateChanged)
    }

    override fun setupListener() {
        getBinding().rvSavedNews.adapter = newsAdapter
        getBinding().refreshLayout.setOnRefreshListener {
            getAllSavedNews()
        }
        getAllSavedNews()
    }

    private fun getAllSavedNews() {
        job?.cancel()
        job = viewLifecycleOwner.lifecycleScope.launch {
            viewModel.newsDao.getAllSavedNews().collectLatest {
                viewModel.savedNews.value = it
                viewModel.getAllSavedNews()
            }
        }

    }

    private fun onStateChanged(state: SavedNewsViewModel.State) {
        when (state) {
            is SavedNewsViewModel.State.OnNewsFetched -> submitRecycler(state.list)
        }
    }

    private fun submitRecycler(itemList: List<RssPaginationItem>) {
        newsAdapter.submitList(itemList)
        getBinding().refreshLayout.isRefreshing = false
    }

    private fun newsClickListener(item: RssPaginationItem) {
        findNavController().navigate(
            SavedNewsFragmentDirections.actionSavedNewsFragmentToNewDetailsFragment(
                item
            )
        )
    }

    private fun favClickListener(item: RssPaginationItem) {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(getString(R.string.are_u_sure_delete))
        }
        viewModel.deleteSavedNews(item)
    }

}