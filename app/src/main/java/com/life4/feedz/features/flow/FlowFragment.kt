package com.life4.feedz.features.flow

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.life4.core.core.view.BaseFragment
import com.life4.core.extensions.observe
import com.life4.core.extensions.observeOnce
import com.life4.feedz.R
import com.life4.feedz.databinding.FragmentFlowBinding
import com.life4.feedz.features.home.adapter.NewsAdapter
import com.life4.feedz.models.Item
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FlowFragment : BaseFragment<FragmentFlowBinding, FlowViewModel>(R.layout.fragment_flow) {
    private val viewModel: FlowViewModel by viewModels()
    private val newsAdapter by lazy { NewsAdapter(::newsClickListener) }

    override fun setupDefinition(savedInstanceState: Bundle?) {
        setupViewModel(viewModel)
        observe(viewModel.liveData, ::onStateChanged)
        getNews()
    }

    override fun setupListener() {
        getBinding().rvNews.adapter = newsAdapter
        getBinding().toolbar.setOnClickListener { findNavController().popBackStack() }
        getBinding().refreshLayout.setOnRefreshListener {
            viewModel.getAndSetNews(viewModel.selectedCategory.value)
        }

        getBinding().categoryToggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.button_sport -> {
                        viewModel.selectedCategory.value = 3
                    }
                    R.id.button_tech -> {
                        viewModel.selectedCategory.value = 2
                    }
                    R.id.button_breaking -> {
                        viewModel.selectedCategory.value = 1
                    }
                    R.id.button_all -> {
                        viewModel.selectedCategory.value = 0
                    }
                }
            }

        }

    }

    private fun onStateChanged(state: FlowViewModel.State) {
        when (state) {
            is FlowViewModel.State.OnNewsFetch -> submitRecycler(state.news)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun submitRecycler(res: List<Item?>) {
        Log.d("listeSayi", res.size.toString())
        if (res.isEmpty())
            newsAdapter.submitList(listOf())
        else
            newsAdapter.submitList(res)
        newsAdapter.notifyDataSetChanged()
        getBinding().refreshLayout.isRefreshing = false
    }

    private fun newsClickListener(item: Item) {
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
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getNews(viewModel.selectedCategory.value)
    }
}