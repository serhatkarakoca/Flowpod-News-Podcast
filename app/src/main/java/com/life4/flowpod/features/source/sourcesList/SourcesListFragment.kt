package com.life4.flowpod.features.source.sourcesList

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.life4.core.core.view.BaseFragment
import com.life4.core.extensions.observe
import com.life4.core.extensions.observeOnce
import com.life4.flowpod.R
import com.life4.flowpod.databinding.FragmentSourceListBinding
import com.life4.flowpod.features.source.adapter.SourceListAdapter
import com.life4.flowpod.models.source.RssFeedResponseItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SourcesListFragment :
    BaseFragment<FragmentSourceListBinding, SourcesListViewModel>(R.layout.fragment_source_list) {
    private val viewModel: SourcesListViewModel by viewModels()
    private val sourceAdapter by lazy { SourceListAdapter(::addSourceToPreference) }
    private val args: SourcesListFragmentArgs by navArgs()
    override fun setupData() {
        super.setupData()
        setupViewModel(viewModel)
        observe(viewModel.liveData, ::onStateChanged)

    }

    override fun setupListener() {
        super.setupListener()
        getBinding().rvSources.adapter = sourceAdapter
        getBinding().layoutSave.setOnClickListener {
            viewModel.getSavedSource().observeOnce(this, Observer {
                it?.let {
                    viewModel.deleteSavedSource()
                }
                viewModel.insertSourceToRoom()
            })
        }
    }

    private fun onStateChanged(state: SourcesListViewModel.State) {
        when (state) {
            is SourcesListViewModel.State.OnSourceAdded -> sourceChanged()
        }
    }

    private fun sourceChanged() {
        val snackbar = Snackbar.make(
            requireView(),
            "Haber Kaynaklarınız Güncellenmiştir.",
            Snackbar.ANIMATION_MODE_SLIDE
        )
        snackbar.show()
        getBinding().layoutSave.isVisible = false
    }

    private fun addSourceToPreference(item: RssFeedResponseItem, isChecked: Boolean) {
        if (isChecked) {
            item.isSelected = false
            viewModel.sourcePref.add(item)
        } else
            viewModel.sourcePref.remove(item)

        getBinding().layoutSave.isVisible = viewModel.sourcePref.isEmpty().not()
    }

    private fun sourcesReady(userSource: List<RssFeedResponseItem>?) {
        userSource?.forEach {
            if (viewModel.sourcePref.indexOf(it) != -1)
                it.isSelected = true
        }
        sourceAdapter.submitList(userSource)
    }

    override fun onResume() {
        super.onResume()
        activity?.let {
            it.title = getString(R.string.content_store)
        }
        getViewModel().getSavedSource().observeOnce(this) { sourceModel ->
            sourceModel?.sourceList?.let { source ->
                viewModel.sourcePref.clear()
                viewModel.sourcePref.addAll(source.sourceList)
            }.also {
                sourcesReady(args.sources.toList())
            }

        }
    }
}