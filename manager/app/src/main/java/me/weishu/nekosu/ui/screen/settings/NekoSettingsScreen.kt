package me.weishu.nekosu.ui.screen.settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import me.weishu.nekosu.R
import me.weishu.nekosu.ui.navigation3.LocalNavigator
import me.weishu.nekosu.ui.theme.BackgroundConfig
import me.weishu.nekosu.ui.theme.NekoBackgroundManager
import me.weishu.nekosu.ui.theme.isActive
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import me.weishu.nekosu.ui.theme.rememberNekoUiConfig
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

@Composable
fun NekoSettingsScreen() {
    val navigator = LocalNavigator.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val nekoConfig by rememberNekoUiConfig()

    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(R.string.neko_customization),
                navigationIcon = {
                    IconButton(onClick = { navigator.pop() }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

    val pickImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            scope.launch {
                NekoBackgroundManager.updateGlobalBackground(
                    context,
                    nekoConfig.globalBackground.copy(type = "image", uri = it.toString())
                )
                Toast.makeText(context, "背景已设置", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val pickVideoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            scope.launch {
                if (!NekoBackgroundManager.isVideoUri(context, it)) {
                    Toast.makeText(context, "请选择 MP4 视频文件", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                NekoBackgroundManager.updateGlobalBackground(
                    context,
                    nekoConfig.globalBackground.copy(type = "video", uri = it.toString())
                )
                Toast.makeText(context, "视频背景已设置", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(innerPadding)
            .padding(horizontal = 12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // ---- 全局背景设置 ----
        Card(
            modifier = Modifier.fillMaxWidth(),
            insideMargin = PaddingValues(vertical = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.neko_background_settings),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = colorScheme.onSurfaceVariantSummary
            )
            BasicComponent(
                title = stringResource(R.string.neko_pick_image),
                summary = nekoConfig.globalBackground.type.takeIf { it == "image" }
                    ?.let { "已设置图片" },
                onClick = { pickImageLauncher.launch("image/*") }
            )
            BasicComponent(
                title = stringResource(R.string.neko_pick_video),
                summary = nekoConfig.globalBackground.type.takeIf { it == "video" }
                    ?.let { "已设置视频" },
                onClick = { pickVideoLauncher.launch("video/mp4") }
            )

            if (nekoConfig.globalBackground.isActive()) {
                Text(
                    text = "模糊: ${nekoConfig.globalBackground.blurRadius}dp",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = colorScheme.onSurface
                )
                Slider(
                    value = nekoConfig.globalBackground.blurRadius.toFloat(),
                    onValueChange = {
                        scope.launch {
                            NekoBackgroundManager.updateGlobalBackground(
                                context,
                                nekoConfig.globalBackground.copy(blurRadius = it.toInt())
                            )
                        }
                    },
                    valueRange = 0f..30f,
                    steps = 29,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Text(
                    text = "暗角: ${(nekoConfig.globalBackground.dimAmount * 100).toInt()}%",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = colorScheme.onSurface
                )
                Slider(
                    value = nekoConfig.globalBackground.dimAmount,
                    onValueChange = {
                        scope.launch {
                            NekoBackgroundManager.updateGlobalBackground(
                                context,
                                nekoConfig.globalBackground.copy(dimAmount = it)
                            )
                        }
                    },
                    valueRange = 0f..0.8f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                thickness = 0.5.dp,
                color = colorScheme.outline.copy(alpha = 0.3f)
            )
            BasicComponent(
                title = stringResource(R.string.neko_reset_background),
                onClick = {
                    scope.launch {
                        NekoBackgroundManager.updateGlobalBackground(context, BackgroundConfig())
                        Toast.makeText(context, "背景已重置", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        // ---- UI 布局选择 ----
        Card(
            modifier = Modifier.fillMaxWidth(),
            insideMargin = PaddingValues(vertical = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.neko_ui_mode),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = colorScheme.onSurfaceVariantSummary
            )
            listOf(
                "miuix" to "Miuix (原版)",
                "material" to "Material (原版)",
                "neko" to "NekoUI (新)"
            ).forEach { (value, label) ->
                val isSelected = nekoConfig.uiLayout == value
                BasicComponent(
                    title = label,
                    endActions = {
                        if (isSelected) {
                            androidx.compose.material3.Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    onClick = {
                        scope.launch { NekoBackgroundManager.updateUiLayout(context, value) }
                    }
                )
            }
        }

        // ---- 显示效果设置 ----
        Card(
            modifier = Modifier.fillMaxWidth(),
            insideMargin = PaddingValues(vertical = 8.dp)
        ) {
            Text(
                text = "显示效果",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = colorScheme.onSurfaceVariantSummary
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "毛玻璃效果",
                    modifier = Modifier.weight(1f),
                    color = colorScheme.onSurface
                )
                Switch(
                    checked = nekoConfig.enableGlassmorphism,
                    onCheckedChange = {
                        scope.launch {
                            NekoBackgroundManager.updateDisplaySettings(
                                context,
                                enableGlassmorphism = it
                            )
                        }
                    }
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "卡片阴影",
                    modifier = Modifier.weight(1f),
                    color = colorScheme.onSurface
                )
                Switch(
                    checked = nekoConfig.enableCardShadow,
                    onCheckedChange = {
                        scope.launch {
                            NekoBackgroundManager.updateDisplaySettings(
                                context,
                                enableCardShadow = it
                            )
                        }
                    }
                )
            }
            if (nekoConfig.globalBackground.isActive()) {
                Text(
                    text = "卡片透明度: ${(nekoConfig.cardTransparency * 100).toInt()}%",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = colorScheme.onSurface
                )
                Slider(
                    value = nekoConfig.cardTransparency,
                    onValueChange = {
                        scope.launch {
                            NekoBackgroundManager.updateDisplaySettings(
                                context,
                                cardTransparency = it
                            )
                        }
                    },
                    valueRange = 0.3f..1.0f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
        }

        // ---- 卡片管理 ----
        Card(
            modifier = Modifier.fillMaxWidth(),
            insideMargin = PaddingValues(vertical = 8.dp)
        ) {
            Text(
                text = "卡片管理",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = colorScheme.onSurfaceVariantSummary
            )
            val hiddenCount = nekoConfig.cardConfigs.count { !it.value.isVisible }
            BasicComponent(
                title = "恢复隐藏的卡片",
                summary = if (hiddenCount > 0) "${hiddenCount} 个卡片被隐藏" else null,
                onClick = {
                    scope.launch {
                        NekoBackgroundManager.restoreAllCards(context)
                        Toast.makeText(context, "已恢复所有卡片", Toast.LENGTH_SHORT).show()
                    }
                }
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                thickness = 0.5.dp,
                color = colorScheme.outline.copy(alpha = 0.3f)
            )
            BasicComponent(
                title = "重置所有美化设置",
                onClick = {
                    scope.launch {
                        NekoBackgroundManager.resetAll(context)
                        Toast.makeText(context, "已重置所有美化", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        // ---- 关于 ----
        Card(
            modifier = Modifier.fillMaxWidth(),
            insideMargin = PaddingValues(16.dp)
        ) {
            Text(text = "NekoSU 美化系统", color = colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "基于 NekoSU 美化分支\n支持全局图片/视频背景、全页面卡片自定义、NekoUI 布局",
                fontSize = 12.sp,
                color = colorScheme.onSurfaceVariantSummary
            )
        }
        Spacer(modifier = Modifier.height(80.dp))
        } // Column
    } // Scaffold content
    } // Scaffold
} // function
