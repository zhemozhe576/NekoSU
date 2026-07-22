package me.weishu.nekosu.ui.util

object NekoBootPatcher {

    fun patchBoot(
        bootImagePath: String,
        outputPath: String,
        ksuCompatible: Boolean
    ): String? {
        return try {
            val result = nativePatchBoot(
                bootImagePath,
                outputPath,
                if (ksuCompatible) 1 else 0
            )
            if (result == 0) null else "Boot patch failed with code: $result"
        } catch (e: Exception) {
            e.message ?: "Unknown error"
        }
    }

    fun isKsuKernel(): Boolean {
        return try {
            nativeIsKsuKernel() == 1
        } catch (_: Exception) {
            false
        }
    }

    fun getKsuVersion(): Int {
        return try {
            nativeGetKsuVersion()
        } catch (_: Exception) {
            0
        }
    }

    private external fun nativePatchBoot(
        bootImagePath: String,
        outputPath: String,
        mode: Int
    ): Int

    private external fun nativeIsKsuKernel(): Int

    private external fun nativeGetKsuVersion(): Int
}
