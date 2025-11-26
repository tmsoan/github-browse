package com.anos.network.interceptor

import android.content.SharedPreferences
import com.anos.network.service.GitHubApi
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val apiService: GitHubApi,
    private val sharedPreferences: SharedPreferences
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request {
        return response.request.newBuilder().build()
        /**
         * https://proandroiddev.com/handling-token-expiration-in-retrofit-automatic-token-refresh-with-okhttp-d5673e4d4c41
         */
        /*
        synchronized(this) { // Prevent multiple refresh calls
            val currentAccessToken = sharedPreferences.getString("access_token", null)
            val refreshToken = sharedPreferences.getString("refresh_token", null) ?: return null

            // If the access token changed since the first failed request, retry with new token
            if (currentAccessToken != response.request.header("Authorization")?.removePrefix("Bearer ")) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentAccessToken")
                    .build()
            }

            // Fetch new tokens synchronously
            val newTokensResponse = apiService.fetchNewTokens(refreshToken)
            if (!newTokensResponse.isSuccessful) {
                return null // Refresh failed, trigger logout
            }

            val newTokens = newTokensResponse.body() ?: return null

            // Save new tokens
            sharedPreferences.edit {
                putString("access_token", newTokens.accessToken)
                    .putString("refresh_token", newTokens.refreshToken)
            }

            // Retry the original request with new token
            return response.request.newBuilder()
                .header("Authorization", "Bearer ${newTokens.accessToken}")
                .build()
        }
        */
    }
}