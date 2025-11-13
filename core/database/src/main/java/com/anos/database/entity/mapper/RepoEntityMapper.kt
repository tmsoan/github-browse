package com.anos.database.entity.mapper

import com.anos.database.entity.RepoEntity
import com.anos.model.Repo

object RepoEntityMapper : EntityMapper<List<Repo>, List<RepoEntity>> {
    override fun asEntity(domain: List<Repo>): List<RepoEntity> {
        return domain.map { repoInfo ->
            RepoEntity(
                page = repoInfo.page,
                id = repoInfo.id,
                name = repoInfo.name.orEmpty(),
                fullName = repoInfo.fullName.orEmpty(),
                description = repoInfo.description.orEmpty(),
                owner = repoInfo.owner,
            )
        }
    }

    override fun asDomain(entity: List<RepoEntity>): List<Repo> {
        return entity.map { entity ->
            Repo(
                page = entity.page,
                id = entity.id,
                name = entity.name,
                fullName = entity.fullName,
                description = entity.description,
                owner = entity.owner,
            )
        }
    }
}

fun List<Repo>.asEntity(): List<RepoEntity> {
    return RepoEntityMapper.asEntity(this)
}

fun List<RepoEntity>?.asDomain(): List<Repo> {
    return RepoEntityMapper.asDomain(this.orEmpty())
}
