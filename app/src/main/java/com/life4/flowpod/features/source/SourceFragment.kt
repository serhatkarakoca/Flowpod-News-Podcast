package com.life4.flowpod.features.source

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.life4.core.core.view.BaseFragment
import com.life4.flowpod.R
import com.life4.flowpod.databinding.FragmentSourcesBinding
import com.life4.flowpod.features.source.adapter.ViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SourceFragment :
    BaseFragment<FragmentSourcesBinding, SourceViewModel>(R.layout.fragment_sources) {
    private val viewModel: SourceViewModel by viewModels()
    private var currentFragmentPosition = 0

    override fun setupDefinition(savedInstanceState: Bundle?) {
        setupViewModel(viewModel)
    }

    override fun setupListener() {
        getBinding().viewPager.adapter =
            ViewPagerAdapter(childFragmentManager, requireActivity().lifecycle)
        TabLayoutMediator(getBinding().tabLayout, getBinding().viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.news)
                }
                1 -> {
                    tab.text = getString(R.string.podcasts)
                }
            }
        }.attach()
        getBinding().viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                currentFragmentPosition = position
                changeSearchBarHint()
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentFragmentPosition = position
                changeSearchBarHint()
            }
        })
        getBinding().etSearch.setOnClickListener {
            findNavController().navigate(
                SourceFragmentDirections.actionSourceFragmentToSearchFragment(
                    currentFragmentPosition == 1
                )
            )
        }

    }

    private fun changeSearchBarHint() {
        if (currentFragmentPosition == 0) {
            getBinding().etSearch.hint = getString(R.string.search_news)
        } else
            getBinding().etSearch.hint = getString(R.string.search_podcast)
    }

    override fun onResume() {
        super.onResume()
        activity?.let {
            it.title = getString(R.string.content_store)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}