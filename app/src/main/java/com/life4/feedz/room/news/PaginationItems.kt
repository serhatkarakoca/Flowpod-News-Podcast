package com.life4.feedz.room.news

import android.os.Parcelable
import androidx.room.Entity
import com.life4.feedz.models.rss_.RssPaginationItem
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class PaginationItems(val items: List<RssPaginationItem>) : Parcelable
