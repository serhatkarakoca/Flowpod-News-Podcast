package com.life4.core.models

interface RecyclerItem {
    val id: Long
    var isSelected: Boolean
    override fun equals(other: Any?): Boolean
}
