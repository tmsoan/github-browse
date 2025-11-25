package com.anos.network.di

import android.content.Context
import com.anos.network.BuildConfig
import com.anos.network.interceptor.CacheInterceptor
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

@Module
@Configuration
@ComponentScan("com.anos.network")
internal object NetworkKoinModule {

    @Single
    fun provideOkHttpClient(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
    }

    @Single
    fun providesNetworkJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @Single
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
        }.build()
    }

    @Single
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
}
