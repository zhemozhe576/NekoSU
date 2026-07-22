package me.weishu.nekosu.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberNekoUiConfig(): State<NekoUiConfig> {
    val context = LocalContext.current
    val configState = remember { mutableStateOf(NekoUiConfig()) }

    LaunchedEffect(Unit) {
        NekoBackgroundManager.getConfigFlow(context).collect {
            configState.value = it
        }
    }

    return configState
}
