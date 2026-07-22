package me.weishu.nekosu.ui.component.neko

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.weishu.nekosu.ui.theme.LocalNekoColorScheme

data class NekoNavItem(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector? = null,
    val badge: String? = null
)

@Composable
fun NekoScaffold(
    navItems: List<NekoNavItem>,
    currentPage: Int,
    onPageChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Transparent,
    content: @Composable (PaddingValues) -> Unit
) {
    val colorScheme = LocalNekoColorScheme.current
    Scaffold(
        modifier = modifier,
        containerColor = containerColor,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                NekoFloatingNavBar(
                    items = navItems,
                    currentPage = currentPage,
                    onPageChanged = onPageChanged,
                    backgroundColor = colorScheme.surfaceContainer.copy(alpha = 0.92f),
                    contentColor = colorScheme.primary,
                    unselectedContentColor = colorScheme.onSurfaceVariant
                )
            }
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}

@Composable
private fun NekoFloatingNavBar(
    items: List<NekoNavItem>,
    currentPage: Int,
    onPageChanged: (Int) -> Unit,
    backgroundColor: Color,
    contentColor: Color,
    unselectedContentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = currentPage == index
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.15f else 1f,
                animationSpec = tween(durationMillis = 200),
                label = "nav_scale"
            )
            NavigationBarItem(
                selected = isSelected,
                onClick = { onPageChanged(index) },
                icon = {
                    Icon(
                        imageVector = if (isSelected && item.selectedIcon != null) item.selectedIcon
                        else item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        color = if (isSelected) contentColor else unselectedContentColor
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = contentColor,
                    selectedTextColor = contentColor,
                    unselectedIconColor = unselectedContentColor,
                    unselectedTextColor = unselectedContentColor,
                    indicatorColor = Color.Transparent
                ),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}
