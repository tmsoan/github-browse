package com.anos.network.service

import com.anos.model.ReadmeContent
import com.anos.model.Repo
import com.anos.model.RepoInfo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubApi {

    @POST("/refresh_token")
    suspend fun fetchNewTokens(refreshToken: String): Response<Any>

    @GET("/repositories")
    suspend fun getPublicRepositories(
        @Query("since") since: Int? = null
    ): Response<List<Repo>>

    @GET("repos/{owner}/{repo}")
    suspend fun getRepositoryDetails(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Response<RepoInfo>

    @GET("repos/{owner}/{repo}/contents/README")
    suspend fun getReadMeContent(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Response<ReadmeContent>
}