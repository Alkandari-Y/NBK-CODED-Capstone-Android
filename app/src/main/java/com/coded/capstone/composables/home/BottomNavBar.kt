package com.coded.capstone.composables.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import com.coded.capstone.SVG.HomeIcon
import com.coded.capstone.SVG.WalletCreditCard16FilledIcon
import com.coded.capstone.SVG.CalenderIcon
import com.coded.capstone.SVG.CalendarStar16FilledIcon

@Composable
fun BottomNavBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val navItems = listOf(
        NavItem({ modifier, color -> HomeIcon(modifier = modifier, color = color) }, "Home"),
        NavItem({ modifier, color -> WalletCreditCard16FilledIcon(modifier = modifier, color = color) }, "Wallet"),
        NavItem({ modifier, color -> CalenderIcon(modifier = modifier, color = color) }, "Calendar"),
        NavItem({ modifier, color -> CalendarStar16FilledIcon(modifier = modifier, color = color) }, "Products")
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(1f),
        color = Color.Transparent,
        shadowElevation = 24.dp
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(85.dp),
            shape = RoundedCornerShape(
                topStart = 40.dp,
                topEnd = 40.dp,
                bottomStart = 0.dp,
                bottomEnd = 0.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF23272E)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 24.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                navItems.forEachIndexed { index, item ->
                    BottomNavItem(
                        icon = item.icon,
                        label = item.label,
                        isSelected = selectedTab == index,
                        onClick = { onTabSelected(index) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: @Composable (Modifier, Color) -> Unit,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val animatedColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF8EC5FF) else Color(0xFF8E8E93),
        animationSpec = tween(300),
        label = "icon_color"
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .height(48.dp)
            .scale(animatedScale)
            .clip(RoundedCornerShape(24.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .then(
                if (isSelected) {
                    Modifier.background(
                        Color(0xFF8EC5FF).copy(alpha = 0.15f),
                        RoundedCornerShape(24.dp)
                    )
                } else {
                    Modifier
                }
            )
            .padding(horizontal = if (isSelected) 16.dp else 12.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            // Selected item with icon and text
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                icon(
                    Modifier.size(25.dp),
                    animatedColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    color = animatedColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            // Unselected item with only icon
            icon(
                Modifier.size(20.dp),
                animatedColor
            )
        }
    }
}

private data class NavItem(
    val icon: @Composable (Modifier, Color) -> Unit,
    val label: String
)