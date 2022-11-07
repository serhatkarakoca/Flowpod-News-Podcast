package com.life4.flowpod.extensions

import android.content.res.Resources.getSystem

val Int.dp: Int get() = (this * getSystem().displayMetrics.density).toInt()