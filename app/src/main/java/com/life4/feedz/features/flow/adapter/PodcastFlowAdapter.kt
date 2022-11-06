package com.life4.feedz.features.flow.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.life4.feedz.R
import com.life4.feedz.databinding.ItemPodcastFlowBinding
import com.life4.feedz.models.rss_.RssResponse

class PodcastFlowAdapter(val listener: (RssResponse) -> Unit) :
    ListAdapter<RssResponse, PodcastFlowAdapter.SourceViewHolder>(DIFF_UTIL) {

    class SourceViewHolder(
        private val binding: ItemPodcastFlowBinding,
        private val listener: (RssResponse) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RssResponse) {
            binding.item = item
            binding.root.setOnClickListener {

                listener(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourceViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemPodcastFlowBinding>(
            inflater,
            R.layout.item_podcast_flow,
            parent,
            false
        )
        return SourceViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: SourceViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<RssResponse>() {
            override fun areItemsTheSame(
                oldItem: RssResponse,
                newItem: RssResponse
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: RssResponse,
                newItem: RssResponse
            ): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

        }
    }
}