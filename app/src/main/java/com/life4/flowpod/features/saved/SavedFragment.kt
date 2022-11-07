package com.life4.flowpod.features.saved

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.life4.core.core.view.BaseFragment
import com.life4.flowpod.R
import com.life4.flowpod.databinding.FragmentSavedBinding
import com.life4.flowpod.features.saved.adapter.SavedViewPagerAdapter
import com.life4.flowpod.features.source.SourceViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedFragment :
    BaseFragment<FragmentSavedBinding, SourceViewModel>(R.layout.fragment_saved) {
    private val viewModel: SourceViewModel by viewModels()
    private var currentFragmentPosition = 0

    override fun setupDefinition(savedInstanceState: Bundle?) {
        setupViewModel(viewModel)
    }

    override fun setupListener() {
        getBinding().viewPager.adapter =
            SavedViewPagerAdapter(childFragmentManager, requireActivity().lifecycle)
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
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentFragmentPosition = position
            }
        })
        getBinding().toolbar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}