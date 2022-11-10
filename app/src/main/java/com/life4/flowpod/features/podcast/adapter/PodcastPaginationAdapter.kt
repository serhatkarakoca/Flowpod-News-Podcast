package com.life4.flowpod.features.podcast.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.life4.flowpod.R
import com.life4.flowpod.databinding.ItemPodcastBinding
import com.life4.flowpod.models.rss_.RssPaginationItem

class PodcastPaginationAdapter(
    val listener: (RssPaginationItem) -> Unit,
    val trashListener: ((RssPaginationItem) -> Unit)? = null
) : PagingDataAdapter<RssPaginationItem, PodcastPaginationAdapter.SourceViewHolder>(DIFF_UTIL) {

    class SourceViewHolder(
        private val binding: ItemPodcastBinding,
        private val listener: (RssPaginationItem) -> Unit,
        val trashListener: ((RssPaginationItem) -> Unit)? = null
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RssPaginationItem) {
            binding.item = item
            binding.root.setOnClickListener {
                listener(item)
            }
            binding.imgTrash.setOnClickListener {
                trashListener?.let { it1 -> it1(item) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourceViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemPodcastBinding>(
            inflater,
            R.layout.item_podcast,
            parent,
            false
        )
        return SourceViewHolder(binding, listener, trashListener)
    }

    override fun onBindViewHolder(holder: SourceViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<RssPaginationItem>() {
            override fun areItemsTheSame(
                oldItem: RssPaginationItem,
                newItem: RssPaginationItem
            ): Boolean {
                return true
            }

            override fun areContentsTheSame(
                oldItem: RssPaginationItem,
                newItem: RssPaginationItem
            ): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

        }
    }
}