package com.life4.flowpod.features.source.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.life4.flowpod.features.source.newsSource.NewsSourceFragment
import com.life4.flowpod.features.source.podcastSource.PodcastSourceFragment

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                NewsSourceFragment()
            }
            1 -> {
                PodcastSourceFragment()
            }
            else -> {
                Fragment()
            }
        }
    }
}