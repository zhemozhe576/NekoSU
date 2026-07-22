package me.weishu.nekosu.ui.component.choosekmidialog

import androidx.compose.runtime.Composable
import me.weishu.nekosu.ui.LocalUiMode
import me.weishu.nekosu.ui.UiMode

@Composable
fun ChooseKmiDialog(
    show: Boolean,
    onDismissRequest: () -> Unit,
    onSelected: (String?) -> Unit
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> ChooseKmiDialogMiuix(show, onDismissRequest, onSelected)
        UiMode.Material -> ChooseKmiDialogMaterial(show, onDismissRequest, onSelected)
        UiMode.Neko -> ChooseKmiDialogMiuix(show, onDismissRequest, onSelected)
    }
}
