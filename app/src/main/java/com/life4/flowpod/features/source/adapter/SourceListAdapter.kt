package com.life4.flowpod.features.source.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.life4.flowpod.R
import com.life4.flowpod.databinding.ItemSourceListBinding
import com.life4.flowpod.models.source.RssFeedResponseItem

class SourceListAdapter(val listener: (RssFeedResponseItem, Boolean) -> Unit) :
    ListAdapter<RssFeedResponseItem, SourceListAdapter.SourceViewHolder>(DIFF_UTIL) {

    class SourceViewHolder(
        private val binding: ItemSourceListBinding,
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
        val binding = DataBindingUtil.inflate<ItemSourceListBinding>(
            inflater,
            R.layout.item_source_list,
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