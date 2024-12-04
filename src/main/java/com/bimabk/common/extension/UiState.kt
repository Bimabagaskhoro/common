package com.bimabk.common.extension

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.io.IOException

sealed interface UiState<out T> {
    data object Init : UiState<Nothing>
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val error: Throwable? = null) : UiState<Nothing>
}

suspend fun <T> MutableStateFlow<UiState<T>>.asState(action: suspend () -> ApiState<T>) {
    this.update { UiState.Loading }
    try {
        when (val result = action()) {
            is ApiState.Success -> {
                this.update { UiState.Success(result.data) }
            }

            is ApiState.Error -> {
                this.update { UiState.Error(result.error) }
            }
        }
    } catch (error: Throwable) {
        this.update { UiState.Error(error) }
    }
}

suspend fun <T> MutableSharedFlow<UiState<T>>.asState(action: suspend () -> ApiState<T>) {
    this.emit(UiState.Loading)
    try {
        when (val result = action()) {
            is ApiState.Success -> {
                this.emit(UiState.Success(result.data))
            }

            is ApiState.Error -> {
                this.emit(UiState.Error(result.error))
            }
        }
    } catch (error: Throwable) {
        this.emit(UiState.Error(error))
    }
}

fun <T> asFlowState(action: suspend () -> ApiState<T>): Flow<UiState<T>> {
    return callbackFlow {
        send(UiState.Loading)
        try {
            when (val result = action()) {
                is ApiState.Success -> {
                    send(UiState.Success(result.data))
                }

                is ApiState.Error -> {
                    send(UiState.Error(result.error))
                }
            }
        } catch (error: Throwable) {
            send(UiState.Error(error))
        } finally {
            close()
        }
    }
}

fun <T> Flow<UiState<T>>.uiState(scope: CoroutineScope): StateFlow<UiState<T>> {
    return this.stateIn(scope, SharingStarted.Lazily, UiState.Init)
}

inline fun <T> UiState<T>.onInit(action: () -> Unit): UiState<T> =
    apply {
        if (this is UiState.Init) {
            action()
        }
    }

inline fun <T> UiState<T>.onLoading(action: () -> Unit): UiState<T> =
    apply {
        if (this is UiState.Loading) {
            action()
        }
    }

inline fun <T> UiState<T>.onSuccess(action: (data: T) -> Unit): UiState<T> =
    apply {
        if (this is UiState.Success) {
            action(data)
        }
    }

inline fun <T> UiState<T>.onError(action: (error: Throwable?) -> Unit): UiState<T> =
    apply {
        if (this is UiState.Error) {
            action(error)
        }
    }

fun Throwable?.errorConnection(): Boolean =
    this is IOException

fun Throwable?.errorApi(): Boolean =
    this is Exception &&
            this.message?.toIntOrNull() != null

fun Throwable?.errorApi(vararg code: Int): Boolean =
    this is Exception &&
            this.message?.toIntOrNull() != null &&
            (this.message?.toIntOrNull() ?: ERROR_CODE) in code

fun Throwable?.getErrorCode(): Int = this?.message?.toIntOrNull() ?: ERROR_CODE

private const val ERROR_CODE = 500
