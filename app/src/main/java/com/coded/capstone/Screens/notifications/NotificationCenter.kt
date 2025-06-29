package com.coded.capstone.Screens.notifications

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.coded.capstone.data.responses.notification.NotificationResponseDto
import com.coded.capstone.data.responses.notification.NotificationTriggerType
import com.coded.capstone.viewModels.NotificationViewModel
import com.coded.capstone.viewModels.NotificationUiState
import java.time.format.DateTimeFormatter

@Composable
fun NotificationCenter(
    isVisible: Boolean,
    onClose: () -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel: NotificationViewModel = viewModel { NotificationViewModel(context) }
    val uiState by viewModel.notificationsUiState.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()

    LaunchedEffect(isVisible) {
        if (isVisible) {
            viewModel.fetchNotifications()
        }
    }

    val slideOffset by animateFloatAsState(
        targetValue = if (isVisible) 0f else 1f,
        animationSpec = tween(300),
        label = "slideAnimation"
    )

    if (isVisible || slideOffset < 1f) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(10f)
                .graphicsLayer {
                    translationX = slideOffset * size.width
                }
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F172A),
                            Color(0xFF1E293B),
                            Color(0xFF334155)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 50.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Notifications",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        if (unreadCount > 0) {
                            Text(
                                text = "$unreadCount new",
                                fontSize = 14.sp,
                                color = Color(0xFF3B82F6),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.1f),
                                        Color.White.copy(alpha = 0.05f)
                                    )
                                )
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                when (val state = uiState) {
                    is NotificationUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                    is NotificationUiState.Success -> {
                        if (state.notifications.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsNone,
                                        contentDescription = null,
                                        tint = Color(0xFF6366F1),
                                        modifier = Modifier.size(60.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "No Notifications",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.notifications) { notification ->
                                    NotificationItem(
                                        notification = notification,
                                        onClick = {
                                            viewModel.markAsRead(notification.id)
                                            val route = viewModel.getRoute(notification)
                                            navController.navigate(route)
                                            onClose()
                                        }
                                    )
                                }
                            }
                        }
                    }
                    is NotificationUiState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = null,
                                    tint = Color.Red,
                                    modifier = Modifier.size(60.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Error loading notifications",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: NotificationResponseDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!notification.delivered) {
                Color.White.copy(alpha = 0.95f)
            } else {
                Color.White.copy(alpha = 0.85f)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (notification.recommendationId != null) Color(0xFF10B981) else Color(0xFF8EC5FF)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getNotificationIcon(notification),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = getNotificationTitle(notification),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = notification.createdAt.format(DateTimeFormatter.ofPattern("MMM dd, HH:mm")),
                    fontSize = 12.sp,
                    color = Color(0xFF9CA3AF)
                )
            }

            if (!notification.delivered) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color(0xFF3B82F6), shape = CircleShape)
                )
            }
        }
    }
}

fun getNotificationIcon(notification: NotificationResponseDto): androidx.compose.ui.graphics.vector.ImageVector {
    return if (notification.recommendationId != null) {
        Icons.Default.CreditCard
    } else {
        when (notification.triggerType) {
            NotificationTriggerType.GPS -> Icons.Default.LocationOn
            NotificationTriggerType.BEACON -> Icons.Default.Bluetooth
            NotificationTriggerType.EXPIRING_PROMOTION -> Icons.Default.Schedule
            else -> Icons.Default.LocalOffer
        }
    }
}

fun getNotificationTitle(notification: NotificationResponseDto): String {
    return if (notification.recommendationId != null) {
        "Card Suggestion"
    } else {
        when (notification.triggerType) {
            NotificationTriggerType.GPS -> "Promotion Nearby"
            NotificationTriggerType.BEACON -> "Special Offer"
            NotificationTriggerType.EXPIRING_PROMOTION -> "Last Chance"
            else -> "Special Offer"
        }
    }
}