package com.life4.core.core.adapter

import androidx.databinding.ViewDataBinding
import com.life4.core.models.RecyclerItem

interface BindListener<T : RecyclerItem, V : ViewDataBinding> {
    fun onBind(holderBase: BaseViewHolder<V>, model: T, position: Int)
}
