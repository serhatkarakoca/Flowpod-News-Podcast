package com.life4.feedz.extensions

import android.content.res.Resources.getSystem

val Int.dp: Int get() = (this * getSystem().displayMetrics.density).toInt()