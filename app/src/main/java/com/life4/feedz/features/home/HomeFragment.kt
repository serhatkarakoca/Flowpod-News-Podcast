package com.life4.feedz.features.home

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.viewModels
import com.life4.core.core.view.BaseFragment
import com.life4.feedz.R
import com.life4.feedz.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment :
    BaseFragment<FragmentHomeBinding, HomeViewModel>(R.layout.fragment_home) {
    private lateinit var settings: MenuItem
    private val viewModel: HomeViewModel by viewModels()

    override fun setupListener() {

    }

    override fun setupDefinition(savedInstanceState: Bundle?) {
        setupViewModel(viewModel)
        getViewModel().getSiteData("https://sekil.net/feed")
    }
}
