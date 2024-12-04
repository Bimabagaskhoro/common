package com.bimabk.common.extension

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.io.IOException

sealed interface State<out T> {
    object Init : State<Nothing>
    object Loading : State<Nothing>
    data class Success<T>(val data: T) : State<T>
    data class Error(val error: Throwable? = null) : State<Nothing>
}

suspend fun <T> MutableStateFlow<State<T>>.asState(action: suspend () -> T) {
    this.update { State.Loading }
    try {
        val data = action()
        this.update { State.Success(data) }
    } catch (error: Throwable) {
        this.update { State.Error(error) }
    }
}

suspend fun <T> MutableSharedFlow<State<T>>.asState(action: suspend () -> T) {
    this.emit(State.Loading)
    try {
        val data = action()
        this.emit(State.Success(data))
    } catch (error: Throwable) {
        this.emit(State.Error(error))
    }
}

fun <T> asFlow(action: suspend () -> T): Flow<T> {
    return callbackFlow {
        try {
            val data = action()
            send(data)
        } catch (ignore: Throwable) {
            close()
        } finally {
            close()
        }
    }
}

fun <T> asFlowState(action: suspend () -> T): Flow<State<T>> {
    return callbackFlow {
        send(State.Loading)
        try {
            val data = action()
            send(State.Success(data))
        } catch (error: Throwable) {
            send(State.Error(error))
        } finally {
            close()
        }
    }
}

fun <T> Flow<T>.state(scope: CoroutineScope, default: T): StateFlow<T> {
    return this.stateIn(scope, SharingStarted.Lazily, default)
}

fun <T> Flow<State<T>>.uiState(scope: CoroutineScope): StateFlow<State<T>> {
    return this.stateIn(scope, SharingStarted.Lazily, State.Init)
}

fun <T> Flow<T>.share(scope: CoroutineScope): SharedFlow<T> {
    return this.shareIn(scope, SharingStarted.Lazily)
}

fun <T> Flow<State<T>>.uiShare(scope: CoroutineScope): SharedFlow<State<T>> {
    return this.shareIn(scope, SharingStarted.Lazily)
}

inline fun <T> State<T>.onInit(action: () -> Unit): State<T> =
    apply {
        if (this is State.Init) {
            action()
        }
    }

inline fun <T> State<T>.onLoading(action: () -> Unit): State<T> =
    apply {
        if (this is State.Loading) {
            action()
        }
    }

inline fun <T> State<T>.onSuccess(action: (data: T) -> Unit): State<T> =
    apply {
        if (this is State.Success) {
            action(data)
        }
    }

inline fun <T> State<T>.onError(action: (error: Throwable?) -> Unit): State<T> =
    apply {
        if (this is State.Error) {
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
