package com.life4.flowpod.features.usersources.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.life4.flowpod.R
import com.life4.flowpod.databinding.ItemUserResourceBinding
import com.life4.flowpod.models.source.RssFeedResponseItem

class ResourceAdapter(val listener: (RssFeedResponseItem, Boolean) -> Unit) :
    ListAdapter<RssFeedResponseItem, ResourceAdapter.SourceViewHolder>(DIFF_UTIL) {

    class SourceViewHolder(
        private val binding: ItemUserResourceBinding,
        private val listener: (RssFeedResponseItem, Boolean) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RssFeedResponseItem) {
            binding.item = item
            binding.imgRemove.setOnClickListener {
                listener(item, true)
            }
            binding.root.setOnClickListener {
                listener(item, false)
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourceViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemUserResourceBinding>(
            inflater,
            R.layout.item_user_resource,
            parent,
            false
        )
        return SourceViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: SourceViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<RssFeedResponseItem>() {
            override fun areItemsTheSame(
                oldItem: RssFeedResponseItem,
                newItem: RssFeedResponseItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: RssFeedResponseItem,
                newItem: RssFeedResponseItem
            ): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

        }
    }
}