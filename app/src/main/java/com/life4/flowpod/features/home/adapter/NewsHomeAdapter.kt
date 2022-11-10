package com.life4.flowpod.features.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.life4.flowpod.R
import com.life4.flowpod.databinding.ItemNewsHomeBinding
import com.life4.flowpod.models.room.SavedNews
import com.life4.flowpod.models.rss_.RssPaginationItem
import com.life4.flowpod.utils.Presets

class NewsHomeAdapter(
    val listener: (RssPaginationItem) -> Unit,
    val favListener: (RssPaginationItem, Boolean) -> Unit,
    val savedNews: MutableLiveData<List<SavedNews>>,
    val isLogin: Boolean
) :
    PagingDataAdapter<RssPaginationItem, NewsHomeAdapter.NewsViewHolder>(DIFF_UTIL) {

    inner class NewsViewHolder(
        val binding: ItemNewsHomeBinding,
        val listener: (RssPaginationItem) -> Unit,
        val favListener: (RssPaginationItem, Boolean) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RssPaginationItem) {
            binding.news = item

            item.isFavorite =
                savedNews.value?.firstOrNull { it.newsItem?.title == item.title } != null

            binding.root.setOnClickListener {
                listener(item)
            }

            binding.cardFav.setOnClickListener {
                if (isLogin) {
                    item.isFavorite = !item.isFavorite
                    if (item.isFavorite) {
                        binding.imgFav.setImageDrawable(
                            ContextCompat.getDrawable(
                                binding.root.context,
                                R.drawable.ic_favorite
                            )
                        )
                        binding.konfettiView.start(Presets.explode())
                    } else
                        binding.imgFav.setImageDrawable(
                            ContextCompat.getDrawable(
                                binding.root.context,
                                R.drawable.ic_favorite_border
                            )
                        )
                    favListener(item, true)
                } else
                    favListener(item, false)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = DataBindingUtil.inflate<ItemNewsHomeBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_news_home, parent, false
        )
        return NewsViewHolder(binding, listener, favListener)
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