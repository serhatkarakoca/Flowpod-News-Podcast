package com.life4.feedz.features.search

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.life4.core.core.view.BaseFragment
import com.life4.core.extensions.observe
import com.life4.feedz.R
import com.life4.feedz.databinding.FragmentSearchBinding
import com.life4.feedz.models.RssResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment :
    BaseFragment<FragmentSearchBinding, SearchViewModel>(R.layout.fragment_search) {
    private val viewModel: SearchViewModel by viewModels()

    private var job: Job? = null

    override fun setupDefinition(savedInstanceState: Bundle?) {
        super.setupDefinition(savedInstanceState)
        setupViewModel(viewModel)
        observe(viewModel.siteData, ::setSiteData)
        getBinding().progressStatus = viewModel.progressStatus
    }

    private fun setSiteData(data: RssResponse) {
        getBinding().site = MutableLiveData(data)
        getBinding().executePendingBindings()
    }

    override fun setupListener() {
        getBinding().etSearch.addTextChangedListener {
            it?.let {
                job?.cancel()
                if (it.isNotEmpty())
                    job = viewLifecycleOwner.lifecycleScope.launch {
                        delay(1300)
                        viewModel.tryToGetSite(it.toString())
                    }
            }
        }
        getBinding().cardView.setOnClickListener {
            val siteUrl = viewModel.confirmedUrl ?: return@setOnClickListener
            val siteDataItems = viewModel.siteData.value
            findNavController().navigate(
                SearchFragmentDirections.actionSearchFragmentToNewsFragment(
                    siteDataItems,
                    siteUrl
                )
            )

        }

    }

    private fun showKeyboard() {
        val inputMethodManager: InputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(getBinding().etSearch, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onResume() {
        super.onResume()
        getBinding().etSearch.requestFocus()
        showKeyboard()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job = null
        viewModel.indexUrlPath = 0
    }
}