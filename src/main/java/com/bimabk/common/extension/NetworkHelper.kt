package com.bimabk.common.extension

private const val ERROR_EMPTY = "Empty"
private const val ERROR_DATA = "Data"

sealed interface ApiState<out T> {
    data class Success<T>(val data: T) : ApiState<T>
    data class Error(val error: Throwable? = null) : ApiState<Nothing>
}

suspend fun <T, R> processResponse(
    fetchApi: suspend () -> T?, transformData: (T) -> R
): ApiState<R> {
    return try {
        val result = fetchApi() ?: throw Exception(ERROR_EMPTY, Exception(ERROR_DATA))
        ApiState.Success(transformData(result))
    } catch (error: Throwable) {
        ApiState.Error(error)
    }
}
