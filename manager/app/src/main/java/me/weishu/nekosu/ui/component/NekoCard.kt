package me.weishu.nekosu.ui.component

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Height
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Photo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import me.weishu.nekosu.R
import me.weishu.nekosu.ui.theme.CardConfig
import me.weishu.nekosu.ui.theme.NekoBackgroundManager
import me.weishu.nekosu.ui.theme.NekoUiConfig
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NekoCard(
    cardId: String,
    modifier: Modifier = Modifier,
    insideMargin: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    onClick: (() -> Unit)? = null,
    onLongPress: (() -> Unit)? = null,
    showIndication: Boolean = true,
    defaultBgColor: Color = colorScheme.surface,
    nekoConfig: NekoUiConfig = NekoUiConfig(),
    content: @Composable ColumnScope.() -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val cardConfig = nekoConfig.cardConfigs[cardId]

    if (cardConfig?.isVisible == false) return

    var showMenu by remember { mutableStateOf(false) }

    val pickCardImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            scope.launch {
                val newConfig = (cardConfig ?: CardConfig(id = cardId)).copy(
                    backgroundType = "image",
                    backgroundUri = it.toString()
                )
                NekoBackgroundManager.updateCardConfig(context, cardId, newConfig)
            }
        }
    }

    val actualBgColor: Color = when {
        cardConfig?.backgroundType == "color" && cardConfig.backgroundColor.isNotEmpty() -> {
            try {
                Color(android.graphics.Color.parseColor(cardConfig.backgroundColor))
            } catch (_: Exception) {
                defaultBgColor
            }
        }
        cardConfig?.backgroundType == "image" -> Color.Transparent
        nekoConfig.globalBackground.isActive() && nekoConfig.enableGlassmorphism ->
            defaultBgColor.copy(alpha = nekoConfig.cardTransparency)
        else -> defaultBgColor
    }

    val cornerRadius = (cardConfig?.cornerRadius ?: 16).dp

    Box(modifier = modifier) {
        Card(
            modifier = Modifier.fillMaxWidth()
                .then(
                    if (cardConfig?.customHeight != null && cardConfig.customHeight > 0)
                        Modifier.heightIn(min = cardConfig.customHeight.dp) else Modifier
                )
                .then(
                    if (nekoConfig.enableCardShadow) {
                        Modifier.shadow(
                            elevation = if (nekoConfig.globalBackground.isActive()) 8.dp else 2.dp,
                            shape = RoundedCornerShape(cornerRadius),
                            ambientColor = Color.Black.copy(alpha = 0.15f),
                            spotColor = Color.Black.copy(alpha = 0.2f)
                        )
                    } else Modifier
                )
                .pointerInput(cardId) {
                    detectTapGestures(
                        onTap = { onClick?.invoke() },
                        onLongPress = {
                            if (onLongPress != null) onLongPress()
                            else showMenu = true
                        }
                    )
                },
            insideMargin = if (cardConfig?.backgroundType == "image") PaddingValues(0.dp) else insideMargin,
            showIndication = showIndication,
            colors = CardDefaults.defaultColors(
                color = actualBgColor
            )
        ) {
            if (cardConfig?.backgroundType == "image" && cardConfig.backgroundUri.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = cardConfig.backgroundUri,
                            contentScale = ContentScale.Crop
                        ),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize().alpha(0.35f)
                    )
                    Box(
                        modifier = Modifier.matchParentSize()
                            .background(actualBgColor.copy(alpha = 0.45f))
                    )
                    Column(modifier = Modifier.padding(insideMargin)) {
                        this@Column.content()
                    }
                }
            } else if (
                cardConfig?.backgroundType == "gradient"
                && cardConfig.gradientColorStart.isNotEmpty()
                && cardConfig.gradientColorEnd.isNotEmpty()
            ) {
                val startColor = try {
                    Color(android.graphics.Color.parseColor(cardConfig.gradientColorStart))
                } catch (_: Exception) {
                    defaultBgColor
                }
                val endColor = try {
                    Color(android.graphics.Color.parseColor(cardConfig.gradientColorEnd))
                } catch (_: Exception) {
                    defaultBgColor
                }
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(startColor, endColor)))
                ) {
                    Column(modifier = Modifier.padding(insideMargin)) {
                        this@Column.content()
                    }
                }
            } else {
                content()
            }
        }

        AnimatedVisibility(
            visible = showMenu,
            enter = fadeIn() + scaleIn(initialScale = 0.85f),
            exit = fadeOut() + scaleOut(targetScale = 0.85f),
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            NekoCardContextMenu(
                onDismiss = { showMenu = false },
                onCustomImage = {
                    showMenu = false
                    pickCardImageLauncher.launch("image/*")
                },
                onCustomColor = {
                    showMenu = false
                    nekoColorPickerCallback?.invoke(cardId, cardConfig)
                },
                onEditHeight = {
                    showMenu = false
                    nekoCardEditCallback?.invoke(cardId, cardConfig)
                },
                onDelete = {
                    showMenu = false
                    scope.launch { NekoBackgroundManager.hideCard(context, cardId) }
                }
            )
        }

        if (showMenu) {
            Box(
                modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                    detectTapGestures { showMenu = false }
                }
            )
        }
    }
}

private var nekoColorPickerCallback: ((String, CardConfig?) -> Unit)? = null
private var nekoCardEditCallback: ((String, CardConfig?) -> Unit)? = null

fun registerNekoColorPickerCallback(callback: (String, CardConfig?) -> Unit) {
    nekoColorPickerCallback = callback
}

fun registerNekoCardEditCallback(callback: (String, CardConfig?) -> Unit) {
    nekoCardEditCallback = callback
}

@Composable
private fun NekoCardContextMenu(
    onDismiss: () -> Unit,
    onCustomImage: () -> Unit,
    onCustomColor: () -> Unit,
    onEditHeight: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val menuItems = remember {
        listOf(
            ContextMenuItem(
                context.getString(R.string.neko_custom_image),
                Icons.Rounded.Photo,
                onCustomImage,
                false
            ),
            ContextMenuItem(
                context.getString(R.string.neko_custom_color),
                Icons.Rounded.Palette,
                onCustomColor,
                false
            ),
            ContextMenuItem(
                context.getString(R.string.neko_edit_height),
                Icons.Rounded.Height,
                onEditHeight,
                false
            ),
            ContextMenuItem(
                context.getString(R.string.neko_delete_card),
                Icons.Rounded.Delete,
                onDelete,
                true
            )
        )
    }

    Column(
        modifier = Modifier
            .padding(top = 8.dp, end = 8.dp)
            .width(200.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(colorScheme.surface.copy(alpha = 0.96f))
            .padding(vertical = 6.dp)
    ) {
        menuItems.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(onClick = item.onClick)
                    .padding(horizontal = 18.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    modifier = Modifier.size(21.dp),
                    tint = if (item.isDangerous) Color(0xFFFF5252) else colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = item.label,
                    fontSize = 14.sp,
                    color = if (item.isDangerous) Color(0xFFFF5252) else colorScheme.onSurface
                )
            }
            if (index < menuItems.lastIndex) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                        .size(width = 0.dp, height = 0.5.dp)
                        .background(colorScheme.outline.copy(alpha = 0.3f))
                )
            }
        }
    }
}

private data class ContextMenuItem(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
    val isDangerous: Boolean
)

@Composable
fun <T> NekoCardItemWrapper(
    item: T,
    cardIdProvider: (T) -> String,
    nekoConfig: NekoUiConfig,
    modifier: Modifier = Modifier,
    insideMargin: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    onClick: (T) -> Unit,
    onLongPress: ((T) -> Unit)? = null,
    content: @Composable ColumnScope.(T) -> Unit
) {
    val cardId = cardIdProvider(item)
    NekoCard(
        cardId = cardId,
        modifier = modifier,
        insideMargin = insideMargin,
        onClick = { onClick(item) },
        onLongPress = onLongPress?.let { { it(item) } },
        nekoConfig = nekoConfig
    ) {
        content(item)
    }
}
