package com.life4.feedz.features.flow.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.life4.feedz.features.flow.NewsFlowFragment
import com.life4.feedz.features.flow.PodcastFlowFragment

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