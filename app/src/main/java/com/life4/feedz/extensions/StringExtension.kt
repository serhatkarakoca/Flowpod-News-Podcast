package com.life4.feedz.extensions

import android.net.Uri

fun String?.toUri(): Uri = this?.let { Uri.parse(it) } ?: Uri.EMPTY
