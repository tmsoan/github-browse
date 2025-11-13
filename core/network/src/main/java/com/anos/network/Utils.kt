package com.anos.network

import com.anos.model.result.NetworkResult
import retrofit2.HttpException
import retrofit2.Response

suspend fun <T : Any> safeApiCall(
    apiExecute: suspend () -> Response<T>,
): NetworkResult<T> {
    return try {
        val response = apiExecute()
        val body = response.body()
        if (response.isSuccessful && body != null) {
            NetworkResult.Success(response.code(), body)
        } else {
            NetworkResult.Error(response.code(), response.errorBody()?.string())
        }
    } catch (e: HttpException) {
        NetworkResult.Error(e.code(), e.message())
    } catch (e: Throwable) {
        NetworkResult.Exception(e)
    } as NetworkResult<T>
}
