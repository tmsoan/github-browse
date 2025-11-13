package com.anos.database.entity.mapper

import com.anos.database.entity.RepoInfoEntity
import com.anos.model.RepoInfo

object RepoInfoEntityMapper : EntityMapper<List<RepoInfo>, List<RepoInfoEntity>> {
    override fun asEntity(domain: List<RepoInfo>): List<RepoInfoEntity> {
        return domain.map { repoInfo ->
            RepoInfoEntity(
                id = repoInfo.id,
                name = repoInfo.name.orEmpty(),
                fullName = repoInfo.fullName.orEmpty(),
                description = repoInfo.description.orEmpty(),
                homepage = repoInfo.homepage.orEmpty(),
                language = repoInfo.language.orEmpty(),
                forksCount = repoInfo.forksCount,
                stargazersCount = repoInfo.stargazersCount,
                subscribersCount = repoInfo.subscribersCount,
                visibility = repoInfo.visibility.orEmpty(),
                openIssues = repoInfo.openIssues,
                topics = repoInfo.topics.orEmpty(),
                url = repoInfo.url.orEmpty(),
                updatedAt = repoInfo.updatedAt.orEmpty(),
                owner = repoInfo.owner,
            )
        }
    }

    override fun asDomain(entity: List<RepoInfoEntity>): List<RepoInfo> {
        return entity.map { entity ->
            RepoInfo(
                id = entity.id,
                name = entity.name,
                fullName = entity.fullName,
                description = entity.description,
                homepage = entity.homepage,
                language = entity.language,
                forksCount = entity.forksCount,
                stargazersCount = entity.stargazersCount,
                subscribersCount = entity.subscribersCount,
                visibility = entity.visibility,
                openIssues = entity.openIssues,
                topics = entity.topics,
                url = entity.url,
                updatedAt = entity.updatedAt,
                owner = entity.owner,
            )
        }
    }
}

fun List<RepoInfo>.asEntity(): List<RepoInfoEntity> {
    return RepoInfoEntityMapper.asEntity(this)
}

fun List<RepoInfoEntity>?.asDomain(): List<RepoInfo> {
    return RepoInfoEntityMapper.asDomain(this.orEmpty())
}
