package com.life4.core.data.remote

import com.life4.core.models.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import retrofit2.Response
import timber.log.Timber

abstract class BaseDataSource {
    protected fun <T> getResult(call: suspend () -> Response<T>): Flow<Resource<T>> = flow {
        try {
            val response = call.invoke()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) emit(Resource.success(body))
                else emit(error(response.errorBody().toString()))
            } else
                emit(error(" ${response.code()} ${response.message()}"))
        } catch (e: Exception) {
            emit(error(e.message ?: e.toString()))
        }
    }.onStart { emit(Resource.loading()) }.flowOn(Dispatchers.IO)

    private fun <T> error(message: String?): Resource<T> {
        Timber.d(message)
        return Resource.error(Throwable("Network call has failed for a following reason: $message"))
    }
}
