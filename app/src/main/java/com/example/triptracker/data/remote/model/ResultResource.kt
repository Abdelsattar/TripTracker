package com.example.triptracker.data.remote.model

import okhttp3.ResponseBody

sealed class ResultResource<out T> {

    data class Success<out T>(val value: T) : ResultResource<T>()

    data class Failure(
        val isNetworkError: Boolean,
        val errorCode: Int?,
        val errorBody: String?
    ) : ResultResource<Nothing>()

    object Loading : ResultResource<Nothing>()
}