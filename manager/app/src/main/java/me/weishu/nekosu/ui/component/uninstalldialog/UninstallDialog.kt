package me.weishu.nekosu.ui.component.uninstalldialog

import androidx.compose.runtime.Composable
import me.weishu.nekosu.ui.LocalUiMode
import me.weishu.nekosu.ui.UiMode

@Composable
fun UninstallDialog(
    show: Boolean,
    onDismissRequest: () -> Unit
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> UninstallDialogMiuix(show, onDismissRequest)
        UiMode.Material -> UninstallDialogMaterial(show, onDismissRequest)
        UiMode.Neko -> UninstallDialogMiuix(show, onDismissRequest)
    }
}
