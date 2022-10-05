package com.life4.feedz.features.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.life4.feedz.R
import com.life4.feedz.databinding.ItemNewsBinding
import com.life4.feedz.models.Item

class NewsAdapter(val listener: (Item) -> Unit) :
    ListAdapter<Item, NewsAdapter.NewsViewHolder>(DIFF_UTIL) {

    class NewsViewHolder(val binding: ItemNewsBinding, val listener: (Item) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
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
        holder.bind(currentList[position])
    }

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                return true
            }

        }
    }
}