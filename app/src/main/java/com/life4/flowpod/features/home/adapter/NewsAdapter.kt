package com.life4.flowpod.features.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.life4.flowpod.R
import com.life4.flowpod.databinding.ItemNewsBinding
import com.life4.flowpod.models.rss_.RssPaginationItem

class NewsAdapter(val listener: (RssPaginationItem) -> Unit) :
    PagingDataAdapter<RssPaginationItem, NewsAdapter.NewsViewHolder>(DIFF_UTIL) {

    class NewsViewHolder(val binding: ItemNewsBinding, val listener: (RssPaginationItem) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RssPaginationItem) {
            binding.item = item
            binding.root.setOnClickListener {
                listener(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = DataBindingUtil.inflate<ItemNewsBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_news, parent, false
        )
        return NewsViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<RssPaginationItem>() {
            override fun areItemsTheSame(
                oldItem: RssPaginationItem,
                newItem: RssPaginationItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: RssPaginationItem,
                newItem: RssPaginationItem
            ): Boolean {
                return oldItem.title == newItem.title
            }

        }
    }
}