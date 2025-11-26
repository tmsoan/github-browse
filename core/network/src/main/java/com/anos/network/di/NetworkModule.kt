package com.anos.network.di

import android.content.Context
import com.anos.network.BuildConfig
import com.anos.network.interceptor.AuthInterceptor
import com.anos.network.interceptor.CacheInterceptor
import com.anos.network.interceptor.TokenAuthenticator
import com.anos.network.service.RemoteDataSource
import com.anos.network.service.GitHubApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
    }

    @Provides
    @Singleton
    fun providesNetworkJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    fun provideCacheInterceptor(): CacheInterceptor {
        return CacheInterceptor()
    }

    @Provides
    @Singleton
    fun provideOkHttp(
        applicationContext: Context,
        builder: OkHttpClient.Builder,
        cacheInterceptor: CacheInterceptor,
    ): OkHttpClient {
        // define a cache for network responses
        val cacheSize = (5 * 1024 * 1024).toLong() // 5 MB
        val cache = Cache(applicationContext.cacheDir, cacheSize)

        return builder.apply {
            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BODY
                addInterceptor(logging)
            }
            cache(cache)
            addInterceptor(cacheInterceptor)
            connectTimeout(10, TimeUnit.SECONDS)
            readTimeout(10, TimeUnit.SECONDS)
            // refresh token
            /*addInterceptor(AuthInterceptor(sharedPreferences))
            authenticator(TokenAuthenticator(apiService, sharedPreferences))*/
        }.build()
    }

    @Provides
    @Singleton
    fun provideRetrofitClient(
        okHttpClient: OkHttpClient,
        json: Json,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideGitHubApi(retrofit: Retrofit): GitHubApi {
        return retrofit.create(GitHubApi::class.java)
    }

    @Provides
    @Singleton
    fun provideApiDataSource(gitHubApi: GitHubApi): RemoteDataSource {
        return RemoteDataSource(gitHubApi)
    }
}
