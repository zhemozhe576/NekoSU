package me.weishu.nekosu.ui.theme

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val Context.nekoDataStore: DataStore<Preferences> by preferencesDataStore(name = "nekosu_settings")

@Serializable
data class BackgroundConfig(
    val type: String = "none",
    val uri: String = "",
    val blurRadius: Int = 0,
    val dimAmount: Float = 0.35f,
    val scaleType: String = "crop"
)

fun BackgroundConfig.isActive(): Boolean = type != "none" && uri.isNotEmpty()

@Serializable
data class CardConfig(
    val id: String = "",
    val backgroundType: String = "default",
    val backgroundUri: String = "",
    val backgroundColor: String = "",
    val gradientColorStart: String = "",
    val gradientColorEnd: String = "",
    val customHeight: Int = 0,
    val position: Int = -1,
    val isVisible: Boolean = true,
    val cornerRadius: Int = 16,
    val alpha: Float = 1.0f
)

@Serializable
data class NekoUiConfig(
    val globalBackground: BackgroundConfig = BackgroundConfig(),
    val cardConfigs: Map<String, CardConfig> = emptyMap(),
    val uiLayout: String = "miuix",
    val enableGlassmorphism: Boolean = true,
    val cardTransparency: Float = 0.72f,
    val enableCardShadow: Boolean = true
)

object NekoBackgroundManager {

    private val UI_CONFIG_KEY = stringPreferencesKey("neko_ui_config_v2")
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun getConfigFlow(context: Context): Flow<NekoUiConfig> {
        return context.nekoDataStore.data.map { prefs ->
            prefs[UI_CONFIG_KEY]?.let {
                try {
                    json.decodeFromString(it)
                } catch (_: Exception) {
                    NekoUiConfig()
                }
            } ?: NekoUiConfig()
        }
    }

    suspend fun saveConfig(context: Context, config: NekoUiConfig) {
        context.nekoDataStore.edit { prefs ->
            prefs[UI_CONFIG_KEY] = json.encodeToString(config)
        }
    }

    suspend fun updateGlobalBackground(context: Context, background: BackgroundConfig) {
        context.nekoDataStore.edit { prefs ->
            val current = getCurrentConfig(prefs)
            prefs[UI_CONFIG_KEY] = json.encodeToString(current.copy(globalBackground = background))
        }
    }

    suspend fun updateCardConfig(context: Context, cardId: String, cardConfig: CardConfig) {
        context.nekoDataStore.edit { prefs ->
            val current = getCurrentConfig(prefs)
            val newCards = current.cardConfigs.toMutableMap()
            newCards[cardId] = cardConfig.copy(id = cardId)
            prefs[UI_CONFIG_KEY] = json.encodeToString(current.copy(cardConfigs = newCards))
        }
    }

    suspend fun removeCardConfig(context: Context, cardId: String) {
        context.nekoDataStore.edit { prefs ->
            val current = getCurrentConfig(prefs)
            val newCards = current.cardConfigs.toMutableMap()
            newCards.remove(cardId)
            prefs[UI_CONFIG_KEY] = json.encodeToString(current.copy(cardConfigs = newCards))
        }
    }

    suspend fun hideCard(context: Context, cardId: String) {
        context.nekoDataStore.edit { prefs ->
            val current = getCurrentConfig(prefs)
            val existing = current.cardConfigs[cardId] ?: CardConfig(id = cardId)
            val newCards = current.cardConfigs.toMutableMap()
            newCards[cardId] = existing.copy(isVisible = false)
            prefs[UI_CONFIG_KEY] = json.encodeToString(current.copy(cardConfigs = newCards))
        }
    }

    suspend fun restoreAllCards(context: Context) {
        context.nekoDataStore.edit { prefs ->
            val current = getCurrentConfig(prefs)
            val newCards = current.cardConfigs.mapValues { (_, v) -> v.copy(isVisible = true) }
            prefs[UI_CONFIG_KEY] = json.encodeToString(current.copy(cardConfigs = newCards))
        }
    }

    suspend fun updateUiLayout(context: Context, layout: String) {
        context.nekoDataStore.edit { prefs ->
            val current = getCurrentConfig(prefs)
            prefs[UI_CONFIG_KEY] = json.encodeToString(current.copy(uiLayout = layout))
        }
    }

    suspend fun updateDisplaySettings(
        context: Context,
        enableGlassmorphism: Boolean? = null,
        cardTransparency: Float? = null,
        enableCardShadow: Boolean? = null
    ) {
        context.nekoDataStore.edit { prefs ->
            val current = getCurrentConfig(prefs)
            prefs[UI_CONFIG_KEY] = json.encodeToString(
                current.copy(
                    enableGlassmorphism = enableGlassmorphism ?: current.enableGlassmorphism,
                    cardTransparency = cardTransparency ?: current.cardTransparency,
                    enableCardShadow = enableCardShadow ?: current.enableCardShadow
                )
            )
        }
    }

    suspend fun resetAll(context: Context) {
        context.nekoDataStore.edit { prefs ->
            prefs[UI_CONFIG_KEY] = json.encodeToString(NekoUiConfig())
        }
    }

    fun getVideoThumbnail(context: Context, uri: Uri): Bitmap? {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, uri)
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                retriever.getScaledFrameAtTime(-1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC, 512, 512)
            } else {
                retriever.frameAtTime
            }
            retriever.release()
            bitmap
        } catch (_: Exception) {
            null
        }
    }

    fun isVideoUri(context: Context, uri: Uri): Boolean {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, uri)
            val hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO)
            retriever.release()
            hasVideo != null
        } catch (_: Exception) {
            false
        }
    }

    fun getVideoDuration(context: Context, uri: Uri): Long {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, uri)
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            retriever.release()
            duration?.toLongOrNull() ?: 0L
        } catch (_: Exception) {
            0L
        }
    }

    private fun getCurrentConfig(prefs: Preferences): NekoUiConfig {
        return prefs[UI_CONFIG_KEY]?.let { s ->
            try {
                json.decodeFromString(s)
            } catch (_: Exception) {
                NekoUiConfig()
            }
        } ?: NekoUiConfig()
    }
}
