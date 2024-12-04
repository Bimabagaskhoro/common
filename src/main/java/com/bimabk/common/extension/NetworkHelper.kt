package com.bimabk.common.extension

import retrofit2.HttpException

private const val ERROR_EMPTY = "Empty"
private const val ERROR_DATA = "Data"

suspend fun <T> processResponse(action: suspend () -> T?): T {
    return try {
        action() ?: throw Exception(ERROR_EMPTY, Exception(ERROR_DATA))
    } catch (error: HttpException) {
        throw Exception("${error.code()}", Exception(error.message()))
    }
}