package me.weishu.nekosu.data.repository

import me.weishu.nekosu.data.model.RepoModule

interface ModuleRepoRepository {
    suspend fun fetchModules(): Result<List<RepoModule>>
}
