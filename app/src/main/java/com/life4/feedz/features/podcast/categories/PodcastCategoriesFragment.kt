package com.life4.feedz.features.podcast.categories

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.life4.core.core.view.BaseFragment
import com.life4.feedz.R
import com.life4.feedz.databinding.FragmentPodcastCategoriesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PodcastCategoriesFragment :
    BaseFragment<FragmentPodcastCategoriesBinding, PodcastCategoriesViewModel>(
        R.layout.fragment_podcast_categories
    ) {
    private val viewModel: PodcastCategoriesViewModel by viewModels()

    override fun setupDefinition(savedInstanceState: Bundle?) {
        setupViewModel(viewModel)
    }

    override fun setupListener() {

    }
}