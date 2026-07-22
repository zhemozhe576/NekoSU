package me.weishu.nekosu.ui.viewmodel

import androidx.compose.runtime.Immutable
import me.weishu.nekosu.ui.UiMode
import me.weishu.nekosu.ui.theme.AppSettings

@Immutable
data class MainActivityUiState(
    val appSettings: AppSettings,
    val pageScale: Float,
    val enableBlur: Boolean,
    val enableFloatingBottomBar: Boolean,
    val enableFloatingBottomBarBlur: Boolean,
    val enableNavigationBadge: Boolean,
    val uiMode: UiMode,
)
