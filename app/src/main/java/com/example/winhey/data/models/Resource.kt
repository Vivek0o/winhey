package com.example.winhey.data.models

sealed class Resource<T> {
    data class Loading<T>(val data: T? = null) : Resource<T>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Failure<T>(val message: String, val data: T? = null) : Resource<T>()
}
