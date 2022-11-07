package com.life4.flowpod.features.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.life4.flowpod.R
import com.life4.flowpod.databinding.ItemPodcastResultBinding
import com.life4.flowpod.models.podcast.Feed

class PodcastResultAdapter(val listener: (Feed) -> Unit) :
    ListAdapter<Feed, PodcastResultAdapter.SourceViewHolder>(DIFF_UTIL) {

    class SourceViewHolder(
        private val binding: ItemPodcastResultBinding,
        private val listener: (Feed) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Feed) {
            binding.item = item
            binding.root.setOnClickListener {

                listener(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourceViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemPodcastResultBinding>(
            inflater,
            R.layout.item_podcast_result,
            parent,
            false
        )
        return SourceViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: SourceViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<Feed>() {
            override fun areItemsTheSame(
                oldItem: Feed,
                newItem: Feed
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: Feed,
                newItem: Feed
            ): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

        }
    }
}