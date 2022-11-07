package com.life4.flowpod.features.saved.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.life4.flowpod.features.saved.SavedNewsFragment
import com.life4.flowpod.features.saved.savedpodcast.SavedPodcastFragment

class SavedViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                SavedNewsFragment()
            }
            1 -> {
                SavedPodcastFragment()
            }
            else -> {
                Fragment()
            }
        }
    }
}