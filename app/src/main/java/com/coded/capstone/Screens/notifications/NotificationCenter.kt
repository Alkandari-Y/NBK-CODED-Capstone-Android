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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.coded.capstone.data.responses.notification.NotificationResponseDto
import com.coded.capstone.data.responses.notification.NotificationTriggerType
import com.coded.capstone.navigation.NavRoutes
import com.coded.capstone.viewModels.NotificationViewModel
import com.coded.capstone.viewModels.NotificationUiState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationCenter(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("General", "Recommended")
    val context = LocalContext.current
    
    // Use real notification data from ViewModel
    val notificationViewModel: NotificationViewModel = viewModel { NotificationViewModel(context) }
    val notificationsUiState by notificationViewModel.notificationsUiState.collectAsState()
    
    // Fetch notifications when screen loads
    LaunchedEffect(Unit) {
        notificationViewModel.fetchNotifications()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notifications",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E1E1E),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { 
                            navController.navigate(NavRoutes.NAV_ROUTE_HOME) {
                                popUpTo(NavRoutes.NAV_ROUTE_HOME) { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF8EC5FF).copy(alpha = 0.2f))
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF8EC5FF),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            when (notificationsUiState) {
                is NotificationUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF8EC5FF))
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
                                Icons.Default.Error,
                                contentDescription = "Error",
                                tint = Color(0xFFE74C3C),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Failed to load notifications",
                                fontSize = 16.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }
                }
                is NotificationUiState.Success -> {
                    val currentState = notificationsUiState as NotificationUiState.Success
                    val notifications = currentState.notifications
                    
                    // Filter notifications based on triggerType
                    val generalNotifications = notifications.filter { 
                        it.triggerType != NotificationTriggerType.POS 
                    }
                    val recommendedNotifications = notifications.filter { 
                        it.triggerType == NotificationTriggerType.POS 
                    }
                    
                    // Tabs
                    TabRow(
                        selectedTabIndex = selectedTab,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        containerColor = Color.White,
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = Color(0xFF8EC5FF)
                            )
                        }
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = title,
                                            color = if (selectedTab == index) Color(0xFF8EC5FF) else Color(0xFF8E8E93),
                                            fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal
                                        )
                                        if (index == 1 && recommendedNotifications.isNotEmpty()) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Box(
                                                modifier = Modifier
                                                    .size(18.dp)
                                                    .background(Color(0xFF8EC5FF), CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = recommendedNotifications.size.toString(),
                                                    color = Color.White,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Notifications List
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    ) {
                        val filteredNotifications = if (selectedTab == 0) {
                            generalNotifications
                        } else {
                            recommendedNotifications
                        }

                        items(filteredNotifications) { notification ->
                            RealNotificationCard(
                                notification = notification,
                                onClick = {
                                    // Mark as read when clicked
                                    notificationViewModel.markAsRead(notification.id)
                                    
                                    // Navigate based on notification type
                                    if (notification.recommendationId != null) {
                                        navController.navigate(NavRoutes.NAV_ROUTE_RECOMMENDATIONS)
                                    } else if (notification.promotionId != null) {
                                        navController.navigate(NavRoutes.promotionDetailsRoute(notification.promotionId))
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        
                        if (filteredNotifications.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            Icons.Default.NotificationsNone,
                                            contentDescription = "No notifications",
                                            tint = Color(0xFF8E8E93),
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "No notifications yet",
                                            fontSize = 16.sp,
                                            color = Color(0xFF8E8E93),
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "You'll see notifications here when they arrive",
                                            fontSize = 14.sp,
                                            color = Color(0xFF8E8E93),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun RealNotificationCard(
    notification: NotificationResponseDto,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F9FA)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = getRealNotificationColor(notification),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getRealNotificationIcon(notification),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = notification.title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Color(0xFF1E1E1E),
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = formatTimeAgo(notification.createdAt),
                        fontSize = 12.sp,
                        color = Color(0xFF8E8E93)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    fontSize = 14.sp,
                    color = Color(0xFF6D6D6D),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Special handling for different notification types
                when (notification.triggerType) {
                    NotificationTriggerType.EXPIRING_PROMOTION -> {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    Color(0xFFE74C3C).copy(alpha = 0.1f),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Expires Soon",
                                fontSize = 12.sp,
                                color = Color(0xFFE74C3C),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    NotificationTriggerType.POS -> {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    Color(0xFF8EC5FF).copy(alpha = 0.1f),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Card Recommendation",
                                fontSize = 12.sp,
                                color = Color(0xFF8EC5FF),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    NotificationTriggerType.GPS -> {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    Color(0xFF10B981).copy(alpha = 0.1f),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Nearby Offer",
                                fontSize = 12.sp,
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    NotificationTriggerType.BEACON -> {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    Color(0xFF8B5CF6).copy(alpha = 0.1f),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Special Offer",
                                fontSize = 12.sp,
                                color = Color(0xFF8B5CF6),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    else -> {
                        // No special badge for unknown types
                    }
                }
            }

            // Unread indicator
            if (!notification.delivered) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFF8EC5FF), CircleShape)
                )
            }
        }
    }
}



fun getRealNotificationIcon(notification: NotificationResponseDto): ImageVector {
    return if (notification.recommendationId != null) {
        Icons.Default.CreditCard
    } else {
        when (notification.triggerType) {
            NotificationTriggerType.GPS -> Icons.Default.LocationOn
            NotificationTriggerType.BEACON -> Icons.Default.Bluetooth
            NotificationTriggerType.EXPIRING_PROMOTION -> Icons.Default.Schedule
            NotificationTriggerType.POS -> Icons.Default.CreditCard
            else -> Icons.Default.LocalOffer
        }
    }
}

fun getRealNotificationColor(notification: NotificationResponseDto): Color {
    return when (notification.triggerType) {
        NotificationTriggerType.GPS -> Color(0xFF10B981)
        NotificationTriggerType.BEACON -> Color(0xFF8B5CF6)
        NotificationTriggerType.EXPIRING_PROMOTION -> Color(0xFFE74C3C)
        NotificationTriggerType.POS -> Color(0xFF8EC5FF)
        else -> Color(0xFF8EC5FF)
    }
}

fun formatTimeAgo(createdAt: java.time.LocalDateTime): String {
    val now = java.time.LocalDateTime.now()
    val minutes = ChronoUnit.MINUTES.between(createdAt, now)
    val hours = ChronoUnit.HOURS.between(createdAt, now)
    val days = ChronoUnit.DAYS.between(createdAt, now)
    
    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}h ago"
        days < 7 -> "${days}d ago"
        else -> createdAt.format(DateTimeFormatter.ofPattern("MMM dd"))
    }
}