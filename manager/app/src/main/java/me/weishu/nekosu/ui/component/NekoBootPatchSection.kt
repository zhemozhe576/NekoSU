package me.weishu.nekosu.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.weishu.nekosu.R
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Switch
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

@Composable
fun NekoBootPatchSection(
    onPatchClick: (isKsuCompatible: Boolean) -> Unit
) {
    var ksuCompatible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.neko_boot_patch_title),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.neko_boot_patch_desc),
            fontSize = 13.sp,
            color = colorScheme.onSurfaceVariantSummary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (ksuCompatible) colorScheme.primaryContainer
                        else colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (ksuCompatible) Icons.Rounded.CheckCircle else Icons.Rounded.Info,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = if (ksuCompatible) colorScheme.primary else colorScheme.onSurfaceVariant
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.neko_ksu_coexist),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onSurface
                )
                Text(
                    text = if (ksuCompatible) {
                        stringResource(R.string.neko_ksu_coexist_on_desc)
                    } else {
                        stringResource(R.string.neko_ksu_coexist_off_desc)
                    },
                    fontSize = 12.sp,
                    color = colorScheme.onSurfaceVariantSummary
                )
            }

            Switch(
                checked = ksuCompatible,
                onCheckedChange = { ksuCompatible = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            text = stringResource(R.string.neko_boot_patch_btn),
            onClick = { onPatchClick(ksuCompatible) },
            modifier = Modifier.fillMaxWidth()
        )

        if (ksuCompatible) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = colorScheme.primary
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = stringResource(R.string.neko_ksu_coexist_hint),
                    fontSize = 11.sp,
                    color = colorScheme.primary
                )
            }
        }
    }
}
