package me.weishu.nekosu.data.repository

import me.weishu.nekosu.data.model.Module
import me.weishu.nekosu.data.model.ModuleUpdateInfo

interface ModuleRepository {
    suspend fun getModules(): Result<List<Module>>
    suspend fun checkUpdate(module: Module): Result<ModuleUpdateInfo>
}
