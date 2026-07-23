package me.weishu.nekosu.ui.component

import androidx.compose.runtime.Composable
import me.weishu.nekosu.Natives
import me.weishu.nekosu.data.repository.SettingsRepositoryImpl
import me.weishu.nekosu.ui.util.isFullFeatured

@Composable
fun KsuIsValid(
    content: @Composable () -> Unit
) {
    if (isFullFeatured()) {
        content()
    }
}
