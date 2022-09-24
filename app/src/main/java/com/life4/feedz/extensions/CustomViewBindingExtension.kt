package com.life4.feedz.extensions

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.imageview.ShapeableImageView
import com.life4.core.extensions.dismiss
import com.life4.core.extensions.show
import com.life4.feedz.app.GlideApp

@BindingAdapter("setImage")
fun ShapeableImageView.bindImage(res: String?) {
    if (res.isNullOrEmpty())
        return
    GlideApp
        .with(this.context)
        .load(res)
        .into(this)
}

@BindingAdapter("setAdapterRecyclerAdapter")
fun RecyclerView.bindRecyclerViewAdapter(adapter: RecyclerView.Adapter<*>?) {
    if (adapter == null)
        return
    this.setHasFixedSize(true)
    this.adapter = adapter
}

@BindingAdapter("setAdapterViewPager2Adapter")
fun ViewPager2.bindViewPager2Adapter(adapter: RecyclerView.Adapter<*>?) {
    if (adapter == null)
        return
    this.adapter = adapter
}

@BindingAdapter("setVisibilityForString")
fun View.bindVisibilityForString(value: String?) {
    if (value.isNullOrEmpty())
        this.dismiss()
    else
        this.show()
}
