package com.life4.feedz.features.news

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.life4.core.core.view.BaseFragment
import com.life4.core.extensions.observe
import com.life4.core.extensions.observeOnce
import com.life4.feedz.R
import com.life4.feedz.databinding.FragmentNewsBinding
import com.life4.feedz.features.main.MainActivity
import com.life4.feedz.features.news.adapter.CardNewsAdapter
import com.life4.feedz.models.rss_.RssPaginationItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewsFragment : BaseFragment<FragmentNewsBinding, NewsViewModel>(R.layout.fragment_news) {
    private val viewModel: NewsViewModel by viewModels()
    private val newsAdapter by lazy { CardNewsAdapter(::newsClickListener) }
    override fun setupDefinition(savedInstanceState: Bundle?) {
        super.setupDefinition(savedInstanceState)
        setupViewModel(viewModel)
    }

    override fun setupListener() {
        activity?.let {
            (it as MainActivity).getBinding().addBookmark.setOnClickListener {
                if (viewModel.siteAdded.value == false)
                    viewModel.addSiteToBookmark()
                else
                    viewModel.deleteSource()
            }
        }

        observe(viewModel.siteAdded) {
            (activity as MainActivity).getBinding().addBookmark.setImageDrawable(
                if (it) ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_bookmark_remove
                ) else ContextCompat.getDrawable(requireContext(), R.drawable.ic_bookmark_add)
            )
        }

        getBinding().rvNews.adapter = newsAdapter

        arguments?.let {
            val args = NewsFragmentArgs.fromBundle(it)
            viewModel.siteUrl = args.siteUrl
            viewModel.rssResponse = args.rssResponse
        }

    }

    private fun newsClickListener(item: RssPaginationItem) {
        findNavController().navigate(
            NewsFragmentDirections.actionNewsFragmentToNewDetailsFragment(
                item
            )
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.getSources().observeOnce(this) {
            it?.let {
                viewModel.userSources.value = it
                viewModel.siteAdded.value =
                    it.sourceList?.sourceList?.firstOrNull { it.siteUrl == viewModel.siteUrl } != null
            }
        }
        activity?.let {
            (it as MainActivity).getBinding().addBookmark.visibility = View.VISIBLE
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getSiteData().collectLatest {
                newsAdapter.submitData(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity).getBinding().addBookmark.visibility = View.GONE
    }
}