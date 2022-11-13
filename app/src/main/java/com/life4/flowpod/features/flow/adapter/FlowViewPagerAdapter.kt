package com.life4.flowpod.features.flow.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.life4.flowpod.features.flow.newsflow.NewsFlowFragment
import com.life4.flowpod.features.flow.podcastflow.PodcastFlowFragment

class FlowViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                NewsFlowFragment()
            }
            1 -> {
                PodcastFlowFragment()
            }
            else -> {
                Fragment()
            }
        }
    }
}