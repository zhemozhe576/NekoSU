package me.weishu.nekosu.ui

import androidx.compose.runtime.staticCompositionLocalOf

enum class UiMode(val value: String) {
    Miuix("miuix"),
    Material("material"),
    Neko("neko");

    companion object {
        fun fromValue(value: String): UiMode = when (value) {
            Material.value -> Material
            Neko.value -> Neko
            else -> Miuix
        }

        val DEFAULT_VALUE = Miuix.value
    }
}

val LocalUiMode = staticCompositionLocalOf { UiMode.Miuix }
