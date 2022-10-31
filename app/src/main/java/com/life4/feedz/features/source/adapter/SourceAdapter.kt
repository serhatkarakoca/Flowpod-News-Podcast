package com.life4.feedz.features.source.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.life4.feedz.R
import com.life4.feedz.databinding.ItemSourceBinding
import com.life4.feedz.models.source.RssFeedResponseItem

class SourceAdapter(val listener: (RssFeedResponseItem, Boolean) -> Unit, val limit: Int? = null) :
    ListAdapter<RssFeedResponseItem, SourceAdapter.SourceViewHolder>(DIFF_UTIL) {

    class SourceViewHolder(
        private val binding: ItemSourceBinding,
        private val listener: (RssFeedResponseItem, Boolean) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RssFeedResponseItem) {
            binding.item = item
            binding.root.setOnClickListener {
                binding.checkboxSource.isChecked = !binding.checkboxSource.isChecked
                item.isSelected = binding.checkboxSource.isChecked
                listener(item, binding.checkboxSource.isChecked)
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourceViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemSourceBinding>(
            inflater,
            R.layout.item_source,
            parent,
            false
        )
        return SourceViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: SourceViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun getItemCount(): Int {
        val limit = limit ?: currentList.size
        return currentList.size.coerceAtMost(limit)
    }

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<RssFeedResponseItem>() {
            override fun areItemsTheSame(
                oldItem: RssFeedResponseItem,
                newItem: RssFeedResponseItem
            ): Boolean {
                return true
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