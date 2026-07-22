package me.weishu.nekosu.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.window.WindowDialog

@Composable
fun NekoCardEditDialog(
    show: Boolean,
    cardId: String,
    initialHeight: Int,
    initialCornerRadius: Int,
    initialAlpha: Float,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int, Float) -> Unit,
    onReset: () -> Unit
) {
    var height by remember(show) { mutableStateOf(initialHeight.toFloat()) }
    var corner by remember(show) { mutableStateOf(initialCornerRadius.toFloat()) }
    var alpha by remember(show) { mutableStateOf(initialAlpha) }

    WindowDialog(
        show = show,
        title = "编辑卡片",
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text("高度: ${height.toInt()}dp")
            Slider(
                value = height,
                onValueChange = { height = it },
                valueRange = 0f..400f,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("圆角: ${corner.toInt()}dp")
            Slider(
                value = corner,
                onValueChange = { corner = it },
                valueRange = 0f..32f,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("透明度: ${(alpha * 100).toInt()}%")
            Slider(
                value = alpha,
                onValueChange = { alpha = it },
                valueRange = 0.3f..1.0f,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = onReset) {
                    Text("重置")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onDismiss) {
                    Text("取消")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    onConfirm(height.toInt(), corner.toInt(), alpha)
                }) {
                    Text("确定")
                }
            }
        }
    }
}
