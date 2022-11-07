package com.life4.flowpod.extensions

import android.net.Uri

fun String?.toUri(): Uri = this?.let { Uri.parse(it) } ?: Uri.EMPTY
