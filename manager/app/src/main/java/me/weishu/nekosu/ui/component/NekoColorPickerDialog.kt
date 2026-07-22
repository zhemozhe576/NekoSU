package me.weishu.nekosu.ui.component

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

@Composable
fun NekoColorPickerDialog(
    show: Boolean,
    initialColor: String = "",
    onDismiss: () -> Unit,
    onColorSelected: (String) -> Unit
) {
    if (!show) return

    var hexInput by remember { mutableStateOf(initialColor) }
    var selectedColor by remember { mutableStateOf(initialColor) }

    val presetColors = remember {
        listOf(
            "#FF6750A4", "#FF6200EE", "#FF03DAC5", "#FF018786", "#FFBB86FC", "#FF3700B3",
            "#FF7D5260", "#FFEC407A", "#FFFF5722", "#FF4CAF50", "#FF2196F3", "#FFFFC107",
            "#FF607D8B", "#FF795548", "#FF9C27B0", "#FF3F51B5", "#FF00BCD4", "#FF8BC34A",
            "#FFFF9800", "#FFE91E63", "#00000000", "#80000000", "#F0141218", "#FFFEF7FF"
        )
    }

    OverlayDialog(
        show = show,
        title = "选择颜色",
        onDismissRequest = onDismiss,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            try {
                                if (selectedColor.isNotEmpty())
                                    Color(android.graphics.Color.parseColor(selectedColor))
                                else
                                    colorScheme.surfaceVariant
                            } catch (_: Exception) {
                                colorScheme.surfaceVariant
                            }
                        )
                        .border(
                            1.dp,
                            colorScheme.outline.copy(alpha = 0.3f),
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (selectedColor.isNotEmpty()) selectedColor else "预览",
                        fontSize = 14.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presetColors.forEach { hex ->
                        val color = try {
                            Color(android.graphics.Color.parseColor(hex))
                        } catch (_: Exception) {
                            Color.Transparent
                        }
                        val isSelected = selectedColor.equals(hex, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    if (isSelected) 3.dp else 1.dp,
                                    if (isSelected) colorScheme.primary else colorScheme.outline.copy(
                                        alpha = 0.3f
                                    ),
                                    CircleShape
                                )
                                .clickable { selectedColor = hex; hexInput = hex }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "或输入十六进制颜色码",
                    fontSize = 13.sp,
                    color = colorScheme.onSurfaceVariantSummary
                )
                TextField(
                    value = hexInput,
                    onValueChange = { hexInput = it },
                    label = "#RRGGBB 或 #AARRGGBB"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(text = "取消", onClick = onDismiss, modifier = Modifier.weight(1f))
                    TextButton(
                        text = "确认",
                        onClick = {
                            val finalColor = hexInput.trim().let {
                                if (it.isNotEmpty() && !it.startsWith("#")) "#$it" else it
                            }
                            onColorSelected(finalColor)
                        },
                        colors = ButtonDefaults.textButtonColorsPrimary(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    )
}
