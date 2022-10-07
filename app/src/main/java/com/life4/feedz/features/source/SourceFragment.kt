package com.life4.feedz.features.source

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.life4.core.core.view.BaseFragment
import com.life4.core.extensions.observe
import com.life4.core.extensions.observeOnce
import com.life4.feedz.R
import com.life4.feedz.databinding.FragmentSourcesBinding
import com.life4.feedz.extensions.dp
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
                    viewModel.deleteSavedSource()
                }
                viewModel.insertSourceToRoom()
            })
        }
    }

    private fun addSourceToPreference(item: RssFeedResponseItem, isChecked: Boolean) {
        if (isChecked) {
            item.isSelected = false
            viewModel.sourcePref.add(item)
        } else
            viewModel.sourcePref.remove(item)

        if (viewModel.sourcePref.isEmpty()
                .not() && getBinding().nestedScrollview.paddingBottom == 0
        ) {
            getBinding().nestedScrollview.setPadding(
                0,
                0,
                0,
                75.dp
            )
        } else if (viewModel.sourcePref.isEmpty()) {
            getBinding().nestedScrollview.setPadding(
                0,
                0,
                0,
                0
            )
        }
        getBinding().layoutSave.isVisible = viewModel.sourcePref.isEmpty().not()
    }

    private fun onStateChanged(state: SourceViewModel.State) {
        when (state) {
            is SourceViewModel.State.OnSourceReady -> sourcesReady(state.listMap)
            is SourceViewModel.State.OnSourceAdded -> sourceChanged()
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
        getBinding().nestedScrollview.setPadding(
            0,
            0,
            0,
            0
        )
    }

    private fun sourcesReady(map: MutableMap<Int, List<RssFeedResponseItem>?>) {
        map?.forEach {
            it.value?.forEach {
                Log.d("bulunan", viewModel.sourcePref.toString())
                if (viewModel.sourcePref.indexOf(it) != -1)
                    it.isSelected = true
            }
        }
        breakingAdapter.submitList(map[1])
        techAdapter.submitList(map[2])
        sportAdapter.submitList(map[3])
        getBinding().nestedScrollview.visibility = View.VISIBLE
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
                viewModel.allSources?.let {
                    sourcesReady(it)
                    return@observeOnce
                }
                getViewModel().getBreakingNewsSource()
            }

        }
    }
}