package com.life4.feedz.features.flow

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.life4.core.core.view.BaseFragment
import com.life4.core.extensions.observe
import com.life4.core.extensions.observeOnce
import com.life4.feedz.R
import com.life4.feedz.databinding.BottomSheetFilterBinding
import com.life4.feedz.databinding.FragmentFlowBinding
import com.life4.feedz.features.flow.adapter.FilterAdapter
import com.life4.feedz.features.home.adapter.NewsAdapter
import com.life4.feedz.models.Item
import com.life4.feedz.models.source.RssFeedResponseItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FlowFragment : BaseFragment<FragmentFlowBinding, FlowViewModel>(R.layout.fragment_flow) {
    private val viewModel: FlowViewModel by viewModels()
    private val newsAdapter by lazy { NewsAdapter(::newsClickListener) }
    private val filterAdapter by lazy { FilterAdapter(::filterListener) }
    override fun setupDefinition(savedInstanceState: Bundle?) {
        setupViewModel(viewModel)
        observe(viewModel.liveData, ::onStateChanged)
        getNews()
    }

    private fun filterListener(item: RssFeedResponseItem, isChecked: Boolean) {

    }

    override fun setupListener() {
        getBinding().rvNews.adapter = newsAdapter
        getBinding().toolbar.setOnClickListener { findNavController().popBackStack() }
        getBinding().refreshLayout.setOnRefreshListener {
            viewModel.getAndSetNews(viewModel.selectedCategory.value)
        }

        getBinding().btnFilter.setOnClickListener {
            BottomSheetDialog(requireContext()).apply {
                setCancelable(false)
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
                    viewModel.getAndSetNews(viewModel.selectedCategory.value)
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