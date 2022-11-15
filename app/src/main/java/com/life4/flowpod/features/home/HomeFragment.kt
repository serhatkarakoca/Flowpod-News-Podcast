package com.life4.flowpod.features.home

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.life4.core.core.view.BaseFragment
import com.life4.core.extensions.move
import com.life4.flowpod.R
import com.life4.flowpod.databinding.FragmentHomeBinding
import com.life4.flowpod.features.home.adapter.NewsHomeAdapter
import com.life4.flowpod.features.home.adapter.NewsLoadStateAdapter
import com.life4.flowpod.features.login.LoginActivity
import com.life4.flowpod.models.rss_.RssPaginationItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagingApi::class)
@AndroidEntryPoint
class HomeFragment :
    BaseFragment<FragmentHomeBinding, HomeViewModel>(R.layout.fragment_home) {
    private lateinit var settings: MenuItem
    private val viewModel: HomeViewModel by viewModels()
    private val newsAdapter by lazy {
        NewsHomeAdapter(
            ::newsClickListener,
            ::favClickListener,
            viewModel.savedNews,
            viewModel.isLogin()
        )
    }
    private var pagingJobHome: Job? = null
    private var job: Job? = null


    override fun setupListener() {
        getBinding().rvNews.adapter =
            newsAdapter.withLoadStateFooter(NewsLoadStateAdapter { newsAdapter.retry() })

        getBinding().refreshLayout.setOnRefreshListener {
            //newsAdapter.refresh()
            getBinding().refreshLayout.isRefreshing = false
        }
    }

    override fun setupDefinition(savedInstanceState: Bundle?) {
        setupViewModel(viewModel)
        getAllSavedNews()
        getViewModel().getSources {
            pagingJobHome?.cancel()
            pagingJobHome = lifecycleScope.launch {
                viewModel.getHomeNews().collect { hn ->
                    newsAdapter.submitData(hn)
                }
            }
        }
    }

    private fun getAllSavedNews() {
        job?.cancel()
        job = lifecycleScope.launch {
            viewModel.getAllSavedNews().collectLatest {
                viewModel.savedNews.value = it
            }
        }
    }

    private fun newsClickListener(item: RssPaginationItem) {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToNewDetailsFragment(
                item,
                false
            )
        )
    }

    private fun favClickListener(item: RssPaginationItem, isLogin: Boolean?) {
        if (isLogin == false) {
            requireActivity().move(LoginActivity::class.java, true)
            return
        }
        viewModel.saveNews(item) {
            Snackbar.make(
                requireView(),
                if (it) getText(R.string.saved_success) else getString(R.string.saved_removed),
                Snackbar.ANIMATION_MODE_SLIDE
            ).show()
        }

    }

    override fun onResume() {
        super.onResume()
        getBinding().user =
            FirebaseAuth.getInstance().currentUser?.displayName?.substringBefore(" ")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        job = null
        pagingJobHome = null
    }
}
