package com.life4.feedz.features.source

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.life4.core.core.view.BaseFragment
import com.life4.core.extensions.observe
import com.life4.core.extensions.observeOnce
import com.life4.feedz.R
import com.life4.feedz.databinding.FragmentSourcesBinding
import com.life4.feedz.features.source.adapter.SourceAdapter
import com.life4.feedz.models.source.RssFeedResponseItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SourceFragment :
    BaseFragment<FragmentSourcesBinding, SourceViewModel>(R.layout.fragment_sources) {
    private val viewModel: SourceViewModel by viewModels()
    private val breakingAdapter by lazy { SourceAdapter(::addSourceToPreference) }
    private val techAdapter by lazy { SourceAdapter(::addSourceToPreference) }
    private val sportAdapter by lazy { SourceAdapter(::addSourceToPreference) }

    override fun setupDefinition(savedInstanceState: Bundle?) {
        setupViewModel(viewModel)
    }

    override fun setupListener() {
        observe(viewModel.liveData, ::onStateChanged)
        getBinding().rvBreakingNews.adapter = breakingAdapter
        getBinding().rvTechNews.adapter = techAdapter
        getBinding().rvSportNews.adapter = sportAdapter
        getBinding().layoutSave.setOnClickListener {
            viewModel.getSavedSource().observeOnce(this, Observer {
                it?.let {
                    Log.d("sourcesRoom", it.toString())
                    viewModel.deleteSavedSource(it)
                }
            })
            viewModel.insertSourceToRoom()
        }
    }

    private fun addSourceToPreference(item: RssFeedResponseItem, isChecked: Boolean) {
        if (isChecked)
            viewModel.sourcePref.add(item)
        else
            viewModel.sourcePref.remove(item)

        Log.d("sourcesRoomList", viewModel.sourcePref.toString())
    }

    private fun onStateChanged(state: SourceViewModel.State) {
        when (state) {
            is SourceViewModel.State.OnSourceReady -> sourcesReady(state.listMap)
        }
    }

    private fun sourcesReady(map: MutableMap<Int, List<RssFeedResponseItem>?>) {
        breakingAdapter.submitList(map[1])
        techAdapter.submitList(map[2])
        sportAdapter.submitList(map[3])
    }

    override fun onResume() {
        super.onResume()
        activity?.let {
            it.title = getString(R.string.content_store)
        }
        getViewModel().getBreakingNewsSource()
    }

}