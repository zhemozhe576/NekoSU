package me.weishu.nekosu.ui.component

import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import me.weishu.nekosu.ui.theme.CardConfig
import me.weishu.nekosu.ui.theme.NekoBackgroundManager
import me.weishu.nekosu.ui.theme.rememberNekoUiConfig

@Composable
fun NekoGlobalDialogs() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val nekoConfig by rememberNekoUiConfig()

    var showColorPicker by remember { mutableStateOf(false) }
    var showCardEdit by remember { mutableStateOf(false) }
    var activeCardId by remember { mutableStateOf("") }
    var activeCardConfig by remember { mutableStateOf<CardConfig?>(null) }

    LaunchedEffect(Unit) {
        registerNekoColorPickerCallback { cardId, config ->
            activeCardId = cardId
            activeCardConfig = config
            showColorPicker = true
        }
        registerNekoCardEditCallback { cardId, config ->
            activeCardId = cardId
            activeCardConfig = config
            showCardEdit = true
        }
    }

    NekoColorPickerDialog(
        show = showColorPicker,
        initialColor = activeCardConfig?.backgroundColor ?: "",
        onDismiss = { showColorPicker = false },
        onColorSelected = { color ->
            showColorPicker = false
            scope.launch {
                val newConfig = (activeCardConfig ?: CardConfig(id = activeCardId))
                    .copy(backgroundType = "color", backgroundColor = color)
                NekoBackgroundManager.updateCardConfig(context, activeCardId, newConfig)
            }
        }
    )

    NekoCardEditDialog(
        show = showCardEdit,
        cardId = activeCardId,
        initialHeight = activeCardConfig?.customHeight ?: 0,
        initialCornerRadius = activeCardConfig?.cornerRadius ?: 16,
        initialAlpha = activeCardConfig?.alpha ?: 1.0f,
        onDismiss = { showCardEdit = false },
        onConfirm = { height, corner, alpha ->
            showCardEdit = false
            scope.launch {
                val newConfig = (activeCardConfig ?: CardConfig(id = activeCardId))
                    .copy(customHeight = height, cornerRadius = corner, alpha = alpha)
                NekoBackgroundManager.updateCardConfig(context, activeCardId, newConfig)
            }
        },
        onReset = {
            showCardEdit = false
            scope.launch {
                NekoBackgroundManager.removeCardConfig(context, activeCardId)
            }
        }
    )
}
