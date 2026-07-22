package me.weishu.nekosu.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberNekoUiConfig(): State<NekoUiConfig> {
    val context = LocalContext.current
    return NekoBackgroundManager.getConfigFlow(context).collectAsState(initial = NekoUiConfig())
}
