package com.life4.flowpod.features.podcast.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.life4.flowpod.R
import com.life4.flowpod.databinding.ItemPodcastCategoryBinding
import com.life4.flowpod.models.podcast.categories.Category
import java.util.*

class PodcastCategoryAdapter(val listener: (Category) -> Unit) :
    ListAdapter<Category, PodcastCategoryAdapter.SourceViewHolder>(DIFF_UTIL) {

    class SourceViewHolder(
        private val binding: ItemPodcastCategoryBinding,
        private val listener: (Category) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Category) {
            var currentitem: Category? = item
            val rnd = Random()
            val color: Int = Color.argb(255, rnd.nextInt(220), rnd.nextInt(200), rnd.nextInt(123))
            binding.cardView.setCardBackgroundColor(color)
            when (item.name?.lowercase()) {
                "arts" -> {
                    currentitem = item.copy(name = binding.root.context.getString(R.string.arts))
                }
                "books" -> {
                    currentitem = item.copy(name = binding.root.context.getString(R.string.books))
                }
                "Design" -> {
                    currentitem = item.copy(name = binding.root.context.getString(R.string.design))
                }
                "fashion" -> {
                    currentitem = item.copy(name = binding.root.context.getString(R.string.fashion))
                }
                "beauty" -> {
                    currentitem = item.copy(name = binding.root.context.getString(R.string.beauty))
                }
                "food" -> {
                    currentitem = item.copy(name = binding.root.context.getString(R.string.food))
                }
                "business" -> {
                    currentitem =
                        item.copy(name = binding.root.context.getString(R.string.business))
                }
                "careers" -> {
                    currentitem = item.copy(name = binding.root.context.getString(R.string.careers))
                }
                "investing" -> {
                    currentitem =
                        item.copy(name = binding.root.context.getString(R.string.investing))
                }
                "management" -> {
                    currentitem =
                        item.copy(name = binding.root.context.getString(R.string.management))
                }
                "marketing" -> {
                    currentitem =
                        item.copy(name = binding.root.context.getString(R.string.marketing))
                }
                "comedy" -> {
                    currentitem = item.copy(name = binding.root.context.getString(R.string.comedy))
                }
                "education" -> {
                    currentitem =
                        item.copy(name = binding.root.context.getString(R.string.education))
                }
                "language" -> {
                    currentitem =
                        item.copy(name = binding.root.context.getString(R.string.language))
                }
                "self_improvement" -> {
                    currentitem =
                        item.copy(name = binding.root.context.getString(R.string.self_improvement))
                }
                "history" -> {
                    currentitem = item.copy(name = binding.root.context.getString(R.string.history))
                }
                "health" -> {
                    currentitem = item.copy(name = binding.root.context.getString(R.string.health))
                }
                "stories" -> {
                    currentitem = item.copy(name = binding.root.context.getString(R.string.stories))
                }
                "manga" -> {
                    currentitem = item.copy(name = binding.root.context.getString(R.string.manga))
                }
                "automotive" -> {
                    currentitem =
                        item.copy(name = binding.root.context.getString(R.string.automotive))
                }
                "games" -> {
                    currentitem = item.copy(name = binding.root.context.getString(R.string.games))
                }
                "hobbies" -> {
                    currentitem = item.copy(name = binding.root.context.getString(R.string.hobbies))
                }
                "music" -> {
                    currentitem = item.copy(name = binding.root.context.getString(R.string.music))
                }
                "entertainment" -> {
                    currentitem =
                        item.copy(name = binding.root.context.getString(R.string.entertainment))
                }
                "science" -> {
                    currentitem = item.copy(name = binding.root.context.getString(R.string.science))
                }
                "astronomy" -> {
                    currentitem =
                        item.copy(name = binding.root.context.getString(R.string.astronomy))
                }
                "sports" -> {
                    currentitem = item.copy(name = binding.root.context.getString(R.string.sports))
                }
                "technology" -> {
                    currentitem =
                        item.copy(name = binding.root.context.getString(R.string.technology))
                }
                "film" -> {
                    currentitem = item.copy(name = binding.root.context.getString(R.string.film))
                }
                "cryptocurrency" -> {
                    currentitem =
                        item.copy(name = binding.root.context.getString(R.string.cryptocurrency))
                }
            }

            binding.item = currentitem
            binding.root.setOnClickListener {
                listener(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourceViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemPodcastCategoryBinding>(
            inflater,
            R.layout.item_podcast_category,
            parent,
            false
        )
        return SourceViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: SourceViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<Category>() {
            override fun areItemsTheSame(
                oldItem: Category,
                newItem: Category
            ): Boolean {
                return true
            }

            override fun areContentsTheSame(
                oldItem: Category,
                newItem: Category
            ): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

        }
    }
}