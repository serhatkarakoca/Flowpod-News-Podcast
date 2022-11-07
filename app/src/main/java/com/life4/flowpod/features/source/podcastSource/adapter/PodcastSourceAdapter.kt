package com.life4.flowpod.features.source.podcastSource.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.life4.flowpod.R
import com.life4.flowpod.databinding.ItemPodcastSourceBinding
import com.life4.flowpod.models.podcast.Feed

class PodcastSourceAdapter(val listener: (Feed) -> Unit) :
    ListAdapter<Feed, PodcastSourceAdapter.SourceViewHolder>(DIFF_UTIL) {

    class SourceViewHolder(
        private val binding: ItemPodcastSourceBinding,
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
        val binding = DataBindingUtil.inflate<ItemPodcastSourceBinding>(
            inflater,
            R.layout.item_podcast_source,
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