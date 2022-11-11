package com.life4.flowpod.features.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.life4.flowpod.R
import com.life4.flowpod.databinding.ItemNewsCardHomeBinding
import com.life4.flowpod.databinding.ItemNewsHomeBinding
import com.life4.flowpod.models.room.SavedNews
import com.life4.flowpod.models.rss_.RssPaginationItem

class NewsHomeAdapter(
    val listener: (RssPaginationItem) -> Unit,
    val favListener: (RssPaginationItem, Boolean) -> Unit,
    val savedNews: MutableLiveData<List<SavedNews>>,
    val isLogin: Boolean
) :
    PagingDataAdapter<RssPaginationItem, HomeViewHolder>(DIFF_UTIL) {

    override fun getItemViewType(position: Int): Int {
        return when (position % 5) {
            0 -> R.layout.item_news_home
            else -> R.layout.item_news_card_home
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return when (viewType) {
            R.layout.item_news_home -> {
                val binding = DataBindingUtil.inflate<ItemNewsHomeBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_news_home, parent, false
                )
                HomeViewHolder.NewsViewHolder(binding, savedNews, listener, favListener, isLogin)
            }
            R.layout.item_news_card_home -> {
                val binding = DataBindingUtil.inflate<ItemNewsCardHomeBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_news_card_home, parent, false
                )
                HomeViewHolder.NewsHorizontalViewHolder(
                    binding,
                    savedNews,
                    listener,
                    favListener,
                    isLogin
                )
            }
            else -> throw IllegalStateException("Invalid ViewType")
        }
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        when (position % 5) {
            0 -> getItem(position)?.let {
                (holder as HomeViewHolder.NewsViewHolder).bind(it)
            }
            else -> {
                getItem(position)?.let {
                    (holder as HomeViewHolder.NewsHorizontalViewHolder).bind(it)
                }
            }
        }
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