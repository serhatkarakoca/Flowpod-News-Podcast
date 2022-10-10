package com.life4.feedz.features.flow.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.life4.feedz.R
import com.life4.feedz.databinding.ItemFilterSourceBinding
import com.life4.feedz.models.source.RssFeedResponseItem

class FilterAdapter(val listener: (RssFeedResponseItem, Boolean) -> Unit) :
    ListAdapter<RssFeedResponseItem, FilterAdapter.SourceViewHolder>(DIFF_UTIL) {

    class SourceViewHolder(
        private val binding: ItemFilterSourceBinding,
        private val listener: (RssFeedResponseItem, Boolean) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RssFeedResponseItem) {
            binding.item = item
            binding.root.setOnClickListener {
                binding.checkBox.isChecked = !binding.checkBox.isChecked
                listener(item, binding.checkBox.isChecked)
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourceViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemFilterSourceBinding>(
            inflater,
            R.layout.item_filter_source,
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