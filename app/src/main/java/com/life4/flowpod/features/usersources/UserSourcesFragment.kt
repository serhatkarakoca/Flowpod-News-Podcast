package com.life4.flowpod.features.usersources

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.life4.core.core.view.BaseFragment
import com.life4.core.extensions.observe
import com.life4.core.extensions.observeOnce
import com.life4.flowpod.R
import com.life4.flowpod.databinding.FragmentUserSourcesBinding
import com.life4.flowpod.features.usersources.adapter.ResourceAdapter
import com.life4.flowpod.models.source.RssFeedResponseItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserSourcesFragment :
    BaseFragment<FragmentUserSourcesBinding, UserSourcesViewModel>(R.layout.fragment_user_sources) {
    private val viewModel: UserSourcesViewModel by viewModels()
    private val resourcesAdapter by lazy { ResourceAdapter(::cardClickListener) }

    override fun setupDefinition(savedInstanceState: Bundle?) {
        super.setupDefinition(savedInstanceState)
        setupViewModel(viewModel)
    }

    override fun setupListener() {
        observe(viewModel.userSources) { resourcesAdapter.submitList(it.sourceList?.sourceList) }
        observe(viewModel.liveData, ::onStateChanged)
        getBinding().rvResources.adapter = resourcesAdapter
        getBinding().buttonGoNews.setOnClickListener {
            findNavController().navigate(UserSourcesFragmentDirections.actionGlobalHomeFragment())
        }
    }

    private fun onStateChanged(state: UserSourcesViewModel.State) {
        when (state) {
            is UserSourcesViewModel.State.OnItemRemoved -> deleteItemFromRecycler(state.item)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun deleteItemFromRecycler(item: RssFeedResponseItem) {
        getUserSources()
        resourcesAdapter.notifyDataSetChanged()
    }

    private fun cardClickListener(item: RssFeedResponseItem, isRemove: Boolean) {
        if (isRemove) {

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.Warning))
                .setMessage(getString(R.string.are_u_sure_delete_source))
                .setPositiveButton(
                    getString(R.string.yes),
                    DialogInterface.OnClickListener { dialog, which ->
                        viewModel.deleteSource(item)
                        dialog.dismiss()
                    })
                .setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.dismiss() }
                .show()

            return
        }
        //findNavController().navigate(UserSourcesFragmentDirections.actionUserSourcesFragmentToNewsFragment())
    }

    private fun getUserSources() {
        viewModel.getUserResources().observeOnce(this) {
            viewModel.userSources.value = it
            getBinding().layoutEmpty.isVisible = it?.sourceList?.sourceList.isNullOrEmpty()

        }
    }

    override fun onResume() {
        super.onResume()
        getUserSources()
    }
}