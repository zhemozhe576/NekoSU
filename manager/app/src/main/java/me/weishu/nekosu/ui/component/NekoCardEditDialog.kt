package me.weishu.nekosu.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

@Composable
fun NekoCardEditDialog(
    show: Boolean,
    cardId: String,
    initialHeight: Int = 0,
    initialCornerRadius: Int = 16,
    initialAlpha: Float = 1.0f,
    onDismiss: () -> Unit,
    onConfirm: (height: Int, cornerRadius: Int, alpha: Float) -> Unit,
    onReset: () -> Unit = {}
) {
    if (!show) return

    var height by remember { mutableIntStateOf(initialHeight) }
    var cornerRadius by remember { mutableIntStateOf(initialCornerRadius) }
    var alpha by remember { mutableFloatStateOf(initialAlpha) }

    OverlayDialog(
        show = show,
        title = "卡片设置",
        onDismissRequest = onDismiss,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text(
                        text = "高度: ${if (height == 0) "自适应" else "${height}dp"}",
                        color = colorScheme.onSurface
                    )
                    Slider(
                        value = height.toFloat(),
                        onValueChange = { height = it.toInt() },
                        valueRange = 0f..400f,
                        steps = 39
                    )
                }
                Column {
                    Text(text = "圆角: ${cornerRadius}dp", color = colorScheme.onSurface)
                    Slider(
                        value = cornerRadius.toFloat(),
                        onValueChange = { cornerRadius = it.toInt() },
                        valueRange = 0f..32f,
                        steps = 31
                    )
                }
                Column {
                    Text(text = "透明度: ${(alpha * 100).toInt()}%", color = colorScheme.onSurface)
                    Slider(
                        value = alpha,
                        onValueChange = { alpha = it },
                        valueRange = 0.3f..1.0f
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        text = "重置",
                        onClick = {
                            height = 0
                            cornerRadius = 16
                            alpha = 1.0f
                            onReset()
                        },
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(text = "取消", onClick = onDismiss, modifier = Modifier.weight(1f))
                    TextButton(
                        text = "确认",
                        onClick = { onConfirm(height, cornerRadius, alpha) },
                        colors = ButtonDefaults.textButtonColorsPrimary(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    )
}
