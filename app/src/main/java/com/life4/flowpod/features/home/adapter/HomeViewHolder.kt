package com.life4.flowpod.features.home.adapter

import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.life4.flowpod.R
import com.life4.flowpod.databinding.ItemNewsCardHomeBinding
import com.life4.flowpod.databinding.ItemNewsHomeBinding
import com.life4.flowpod.models.room.SavedNews
import com.life4.flowpod.models.rss_.RssPaginationItem
import com.life4.flowpod.utils.Presets

sealed class HomeViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

    class NewsViewHolder(
        val binding: ItemNewsHomeBinding,
        val savedNews: MutableLiveData<List<SavedNews>>,
        val listener: (RssPaginationItem) -> Unit,
        val favListener: (RssPaginationItem, Boolean) -> Unit,
        val isLogin: Boolean
    ) :
        HomeViewHolder(binding) {
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

    class NewsHorizontalViewHolder(
        val binding: ItemNewsCardHomeBinding,
        val savedNews: MutableLiveData<List<SavedNews>>,
        val listener: (RssPaginationItem) -> Unit,
        val favListener: (RssPaginationItem, Boolean) -> Unit,
        val isLogin: Boolean
    ) : HomeViewHolder(binding) {
        fun bind(item: RssPaginationItem) {
            binding.item = item

            item.isFavorite =
                savedNews.value?.firstOrNull { it.newsItem?.title == item.title } != null

            binding.root.setOnClickListener {
                listener(item)
            }

            binding.imgFav.setOnClickListener {
                if (isLogin) {
                    item.isFavorite = !item.isFavorite
                    if (item.isFavorite) {
                        binding.imgFavorite.setImageDrawable(
                            ContextCompat.getDrawable(
                                binding.root.context,
                                R.drawable.ic_favorite
                            )
                        )
                        binding.konfettiView.start(Presets.explode())
                    } else
                        binding.imgFavorite.setImageDrawable(
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
}