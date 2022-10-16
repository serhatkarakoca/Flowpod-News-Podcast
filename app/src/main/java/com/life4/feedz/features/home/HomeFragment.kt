package com.life4.feedz.features.home

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import com.google.firebase.auth.FirebaseAuth
import com.life4.core.core.view.BaseFragment
import com.life4.feedz.R
import com.life4.feedz.databinding.FragmentHomeBinding
import com.life4.feedz.features.home.adapter.NewsHomeAdapter
import com.life4.feedz.features.home.adapter.NewsLoadStateAdapter
import com.life4.feedz.models.rss_.RssPaginationItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagingApi::class)
@AndroidEntryPoint
class HomeFragment :
    BaseFragment<FragmentHomeBinding, HomeViewModel>(R.layout.fragment_home) {
    private lateinit var settings: MenuItem
    private val viewModel: HomeViewModel by viewModels()
    private val newsAdapter by lazy { NewsHomeAdapter(::newsClickListener) }
    private var pagingJobHome: Job? = null
    override fun setupListener() {
        getBinding().rvNews.adapter =
            newsAdapter.withLoadStateFooter(NewsLoadStateAdapter { newsAdapter.retry() })
    }

    override fun setupDefinition(savedInstanceState: Bundle?) {
        setupViewModel(viewModel)
        getViewModel().getSources {
            pagingJobHome?.cancel()
            pagingJobHome = viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getHomeNews().collect { hn ->
                    newsAdapter.submitData(hn)
                }
            }
        }
    }

    private fun newsClickListener(item: RssPaginationItem) {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToNewDetailsFragment(
                item
            )
        )
    }

    override fun onResume() {
        super.onResume()
        getBinding().user =
            FirebaseAuth.getInstance().currentUser?.displayName?.substringBefore(" ")

    }
}
