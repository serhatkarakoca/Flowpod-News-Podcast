package com.life4.flowpod.extensions

import android.content.Context
import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.imageview.ShapeableImageView
import com.life4.core.extensions.dismiss
import com.life4.core.extensions.show
import com.life4.flowpod.R
import com.life4.flowpod.app.GlideApp


@BindingAdapter("setImage")
fun ShapeableImageView.bindImage(res: String?) {
    if (res.isNullOrEmpty())
        return

    val options = RequestOptions()
        .placeholder(placeHolderProgressBar(this.context))
        .error(R.drawable.placeholder_news)

    GlideApp
        .with(this.context)
        .setDefaultRequestOptions(options)
        .load(res)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)

}

fun placeHolderProgressBar(context: Context): CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 8f
        centerRadius = 40f
        start()
    }
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
