package com.life4.core.manager.resource

import android.content.Context
import android.content.res.Resources

interface MyResourceManager {
    fun getContext(): Context
    fun getResources(): Resources
}
