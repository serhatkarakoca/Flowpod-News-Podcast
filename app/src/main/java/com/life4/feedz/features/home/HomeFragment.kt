package com.life4.feedz.features.home

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.life4.core.core.view.BaseFragment
import com.life4.core.extensions.observe
import com.life4.feedz.R
import com.life4.feedz.databinding.FragmentHomeBinding
import com.life4.feedz.features.home.adapter.NewsAdapter
import com.life4.feedz.models.Item
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment :
    BaseFragment<FragmentHomeBinding, HomeViewModel>(R.layout.fragment_home) {
    private lateinit var settings: MenuItem
    private val viewModel: HomeViewModel by viewModels()
    private val newsAdapter by lazy { NewsAdapter(::newsClickListener) }

    override fun setupListener() {
        getBinding().rvBreakingNews.adapter = newsAdapter
        observe(viewModel.siteDataList) {
            newsAdapter.submitList(it.shuffled())
        }
    }

    override fun setupDefinition(savedInstanceState: Bundle?) {
        setupViewModel(viewModel)
        getViewModel().getSiteData()
    }

    private fun newsClickListener(item: Item) {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToNewDetailsFragment(
                item
            )
        )
    }

}
