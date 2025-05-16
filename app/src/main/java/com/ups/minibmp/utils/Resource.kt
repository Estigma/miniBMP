package com.ups.minibmp.utils

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : Resource<Nothing>()
    data class Loading(val message: String? = null) : Resource<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading

    inline fun onSuccess(action: (T) -> Unit): Resource<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (String, Throwable?) -> Unit): Resource<T> {
        if (this is Error) action(message, throwable)
        return this
    }

    inline fun onLoading(action: (String?) -> Unit): Resource<T> {
        if (this is Loading) action(message)
        return this
    }

    companion object {
        fun <T> success(data: T): Resource<T> = Success(data)
        fun <T> error(message: String, throwable: Throwable? = null): Resource<T> = Error(message, throwable)
        fun <T> loading(message: String? = null): Resource<T> = Loading(message)
    }
}