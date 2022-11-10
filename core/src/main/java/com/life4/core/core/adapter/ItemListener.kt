package com.life4.core.core.adapter

import com.life4.core.models.RecyclerItem

interface ItemListener<T : RecyclerItem> {
    fun onClick(value: T)
    fun onLongClick(value: T) = Unit
}
