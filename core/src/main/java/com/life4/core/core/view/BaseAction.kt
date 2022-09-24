package com.life4.core.core.view

import android.os.Bundle

interface BaseAction {
    fun setupDefinition(savedInstanceState: Bundle?) = Unit
    fun setupData() = Unit
    fun setupListener() = Unit
}
