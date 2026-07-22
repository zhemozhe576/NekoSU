package me.weishu.nekosu.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import me.weishu.nekosu.ui.theme.NekoBackgroundManager
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.window.WindowDialog

@Composable
fun NekoColorPickerDialog(
    show: Boolean,
    initialColor: String,
    onDismiss: () -> Unit,
    onColorSelected: (String) -> Unit
) {
    var selectedColor by remember(show) { mutableStateOf(initialColor) }
    var hexInput by remember(show) { mutableStateOf(initialColor.removePrefix("#")) }

    val colors = listOf(
        "#FF5252", "#FF4081", "#E040FB", "#7C4DFF", "#536DFE",
        "#448AFF", "#40C4FF", "#18FFFF", "#64FFDA", "#69F0AE",
        "#B2FF59", "#EEFF41", "#FFFF00", "#FFD740", "#FFAB40",
        "#FF6E40", "#FF8A80", "#EA80FC", "#B388FF", "#8C9EFF",
        "#82B1FF", "#80D8FF", "#84FFFF", "#A7FFEB", "#B9F6CA",
        "#CCFF90", "#F4FF81", "#FFE57F", "#FFD180", "#FF9E80"
    )

    WindowDialog(
        show = show,
        title = "选择颜色",
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                colors.forEach { colorStr ->
                    val color = try {
                        Color(android.graphics.Color.parseColor(colorStr))
                    } catch (_: Exception) {
                        Color.Gray
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(color)
                            .then(
                                if (selectedColor == colorStr)
                                    Modifier.background(Color.Black.copy(alpha = 0.3f))
                                else Modifier
                            )
                    ) {
                        androidx.compose.foundation.clickable {
                            selectedColor = colorStr
                            hexInput = colorStr.removePrefix("#")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            BasicTextField(
                value = hexInput,
                onValueChange = { hexInput = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorScheme.surfaceVariant)
                    .padding(12.dp),
                textStyle = TextStyle(fontSize = 14.sp, color = colorScheme.onSurface)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = onDismiss) {
                    Text("取消")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    val hex = if (hexInput.startsWith("#")) hexInput else "#$hexInput"
                    onColorSelected(hex)
                }) {
                    Text("确定")
                }
            }
        }
    }
}
