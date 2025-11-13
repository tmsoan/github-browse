package com.anos.model.result

/**
 * A generic class that indicates the state of a network response.
 * @param <T>
 */
sealed class NetworkResult<out T : Any> {
    data class Success<out T : Any>(val code: Int, val data: T) : NetworkResult<T>()
    data class Error(val code: Int, val errorMsg: String?) : NetworkResult<String>()
    data class Exception(val e: Throwable) : NetworkResult<Nothing>()
}

/**
 * Simpler version that executes onSuccess or onFailure based on the result
 * Returns the data on success or null on failure
 *
 * Usage example:
 * ```
 * flow {
 *     val data = remoteDataSource.fetchData()
 *         .getOrHandle(onError)
 *     data?.let { emit(it) }
 * }
 * ```
 */
suspend inline fun <T : Any> NetworkResult<T>.getOrHandle(
    crossinline onFailure: (String?) -> Unit
): T? {
    return when (this) {
        is NetworkResult.Success -> data
        is NetworkResult.Error -> {
            onFailure(errorMsg)
            null
        }
        is NetworkResult.Exception -> {
            onFailure(e.message)
            null
        }
    }
}

/**
 * Even simpler version - returns data on success or throws exception on failure
 *
 * Usage example:
 * ```
 * flow {
 *     try {
 *         val data = remoteDataSource.fetchData().getOrThrow()
 *         emit(data)
 *     } catch (e: Exception) {
 *         onError(e.message)
 *     }
 * }
 * ```
 */
fun <T : Any> NetworkResult<T>.getOrThrow(): T {
    return when (this) {
        is NetworkResult.Success -> data
        is NetworkResult.Error -> throw IllegalStateException("API Error: $errorMsg (code: $code)")
        is NetworkResult.Exception -> throw e
    }
}

/**
 * Usage example:
 * ```
 * flow {
 *     remoteDataSource.fetchData().fold(
 *         onSuccess = { emit(it) },
 *         onFailure = { error -> onError(error) }
 *     )
 * }
 * ```
 */
suspend inline fun <T : Any, R> NetworkResult<T>.fold(
    crossinline onSuccess: suspend (T) -> R,
    crossinline onFailure: suspend (String?) -> R
): R {
    return when (this) {
        is NetworkResult.Success -> onSuccess(data)
        is NetworkResult.Error -> onFailure(errorMsg)
        is NetworkResult.Exception -> onFailure(e.message)
    }
}
