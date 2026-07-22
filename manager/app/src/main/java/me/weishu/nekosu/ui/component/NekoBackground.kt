package me.weishu.nekosu.ui.component

import android.media.MediaPlayer
import android.net.Uri
import android.widget.VideoView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import me.weishu.nekosu.ui.theme.BackgroundConfig

@Composable
fun NekoGlobalBackground(
    config: BackgroundConfig,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize().background(Color.Black)
    ) {
        when (config.type) {
            "image" -> {
                if (config.uri.isNotEmpty()) {
                    val uri = remember(config.uri) { config.uri.toUri() }
                    val scaleType = if (config.scaleType == "fit") ContentScale.Fit else ContentScale.Crop
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = uri,
                            contentScale = ContentScale.Crop
                        ),
                        contentDescription = null,
                        contentScale = scaleType,
                        modifier = Modifier.fillMaxSize()
                            .then(if (config.blurRadius > 0) Modifier.blur(config.blurRadius.dp) else Modifier)
                    )
                }
            }
            "video" -> {
                if (config.uri.isNotEmpty()) {
                    val uri = remember(config.uri) { config.uri.toUri() }
                    NekoVideoBackground(
                        uri = uri,
                        blurRadius = config.blurRadius,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
        if (config.dimAmount > 0f && config.type != "none") {
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(Color.Black.copy(alpha = config.dimAmount))
            )
        }
    }
}

@Composable
private fun NekoVideoBackground(
    uri: Uri,
    blurRadius: Int,
    modifier: Modifier = Modifier
) {
    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }

    AndroidView(
        factory = { ctx ->
            VideoView(ctx).apply {
                setVideoURI(uri)
                setOnPreparedListener { mp ->
                    mp.isLooping = true
                    mp.setVolume(0f, 0f)
                    mp.setScreenOnWhilePlaying(false)
                    start()
                }
                setOnErrorListener { _, _, _ -> true }
                videoViewRef = this
            }
        },
        modifier = modifier.then(
            if (blurRadius > 0) Modifier.blur(blurRadius.dp) else Modifier
        )
    )

    DisposableEffect(uri) {
        onDispose {
            videoViewRef?.stopPlayback()
            videoViewRef = null
        }
    }
}

fun BackgroundConfig.isActive(): Boolean = type != "none" && uri.isNotEmpty()
