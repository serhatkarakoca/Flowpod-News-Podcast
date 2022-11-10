package com.life4.core.data.remote.exception

data class Error(
    override val message: String? = null
) : Throwable() {
    override fun toString(): String {
        return message ?: ""
    }
}
