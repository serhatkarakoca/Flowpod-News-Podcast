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
import com.life4.feedz.features.home.adapter.NewsAdapter
import com.life4.feedz.features.home.adapter.NewsHorizontalAdapter
import com.life4.feedz.models.rss_.RssPaginationItem
import com.life4.feedz.other.Constant
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
    private val newsAdapter by lazy { NewsAdapter(::newsClickListener) }
    private val newsHorizontalAdapter by lazy { NewsHorizontalAdapter(::newsClickListener) }
    private val techNewsHorizontalAdapter by lazy { NewsHorizontalAdapter(::newsClickListener) }
    private val sportNewsHorizontalAdapter by lazy { NewsHorizontalAdapter(::newsClickListener) }
    private var pagingJob: Job? = null
    private var pagingJobTech: Job? = null
    private var pagingJobSport: Job? = null
    override fun setupListener() {
        getBinding().rvBreakingNews.adapter = newsHorizontalAdapter
        getBinding().rvTechNews.adapter = techNewsHorizontalAdapter
        getBinding().rvSportNews.adapter = sportNewsHorizontalAdapter
    }

    override fun setupDefinition(savedInstanceState: Bundle?) {
        setupViewModel(viewModel)
        getViewModel().getSources {
            pagingJob?.cancel()
            pagingJob = viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getFilteredNews(Constant.BREAKING_NEWS).collectLatest { bn ->
                    newsHorizontalAdapter.submitData(bn)
                }
            }
            pagingJobTech?.cancel()
            pagingJobTech = viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getTechNews(Constant.TECH_NEWS).collectLatest { tn ->
                    techNewsHorizontalAdapter.submitData(tn)
                }
            }
            pagingJobSport?.cancel()
            pagingJobSport = viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getSportNews(Constant.SPORT_NEWS).collectLatest { sn ->
                    sportNewsHorizontalAdapter.submitData(sn)
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
