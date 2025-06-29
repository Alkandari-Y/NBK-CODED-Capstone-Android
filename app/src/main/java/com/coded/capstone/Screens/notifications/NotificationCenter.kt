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

// Data ClassesAdd commentMore actions
data class PromotionNotification(
    val name: String,
    val businessName: String,
    val type: RewardType,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val description: String,
    val timeAgo: String
)

data class SuggestedCardNotification(
    val name: String,
    val accountType: String,
    val description: String,
    val interestRate: Double,
    val annualFee: Double,
    val perks: List<String>,
    val timeAgo: String
)

enum class RewardType {
    CASHBACK, DISCOUNT
}

enum class NotificationType {
    PROMOTION_NEARBY,
    SUGGESTED_CARD,
    PROMOTION_ENDING
}

data class NotificationItem(
    val id: String,
    val type: NotificationType,
    val promotionData: PromotionNotification? = null,
    val cardData: SuggestedCardNotification? = null,
    val isRead: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationCenter(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("General", "Recommended")

    val notifications = remember {
        listOf(
            NotificationItem(
                id = "1",
                type = NotificationType.PROMOTION_NEARBY,
                promotionData = PromotionNotification(
                    name = "25% Off Coffee & Pastries",
                    businessName = "Starbucks Downtown",
                    type = RewardType.DISCOUNT,
                    startDate = LocalDate.now(),
                    endDate = LocalDate.now().plusDays(7),
                    description = "Get 25% off all coffee drinks and pastries",
                    timeAgo = "2m ago"
                )
            ),
            NotificationItem(
                id = "2",
                type = NotificationType.SUGGESTED_CARD,
                cardData = SuggestedCardNotification(
                    name = "Premium Rewards Card",
                    accountType = "Credit Card",
                    description = "Earn 3x points on dining and travel purchases",
                    interestRate = 18.9,
                    annualFee = 95.0,
                    perks = listOf("No foreign transaction fees", "Travel insurance", "Priority support"),
                    timeAgo = "5m ago"
                )
            ),
            NotificationItem(
                id = "3",
                type = NotificationType.PROMOTION_ENDING,
                promotionData = PromotionNotification(
                    name = "Double Cashback Weekend",
                    businessName = "Target",
                    type = RewardType.CASHBACK,
                    startDate = LocalDate.now().minusDays(5),
                    endDate = LocalDate.now().plusDays(2),
                    description = "Get 2x cashback on all purchases this weekend",
                    timeAgo = "10m ago"
                )
            ),
            NotificationItem(
                id = "4",
                type = NotificationType.PROMOTION_NEARBY,
                promotionData = PromotionNotification(
                    name = "Free Delivery",
                    businessName = "McDonald's",
                    type = RewardType.DISCOUNT,
                    startDate = LocalDate.now(),
                    endDate = LocalDate.now().plusDays(3),
                    description = "Free delivery on orders over $15",
                    timeAgo = "15m ago"
                )
            ),
            NotificationItem(
                id = "5",
                type = NotificationType.SUGGESTED_CARD,
                cardData = SuggestedCardNotification(
                    name = "Cashback Plus Card",
                    accountType = "Credit Card",
                    description = "Unlimited 1.5% cashback on all purchases",
                    interestRate = 16.9,
                    annualFee = 0.0,
                    perks = listOf("No annual fee", "0% intro APR", "Mobile app"),
                    timeAgo = "1h ago"
                )
            ),
            NotificationItem(
                id = "6",
                type = NotificationType.PROMOTION_ENDING,
                promotionData = PromotionNotification(
                    name = "50% Off First Order",
                    businessName = "Uber Eats",
                    type = RewardType.DISCOUNT,
                    startDate = LocalDate.now().minusDays(10),
                    endDate = LocalDate.now().plusDays(1),
                    description = "Get 50% off your first order (max $20)",
                    timeAgo = "2h ago"
                )
            )
        )
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
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E1E1E)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF8EC5FF))
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1E1E1E)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF1E1E1E),
                    navigationIconContentColor = Color(0xFF1E1E1E)
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
                                if (index == 1) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .background(Color(0xFF8EC5FF), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "2",
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
                    notifications.filter { it.type != NotificationType.SUGGESTED_CARD }
                } else {
                    notifications.filter { it.type == NotificationType.SUGGESTED_CARD }
                }

                items(filteredNotifications) { notification ->
                    NotificationCard(
                        notification = notification,
                        onPromotionClick = {
                            if (notification.type == NotificationType.PROMOTION_NEARBY ||
                                notification.type == NotificationType.PROMOTION_ENDING) {
                                navController.navigate(NavRoutes.NAV_ROUTE_PROMOTION_DETAILS)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    notification: NotificationItem,
    onPromotionClick: () -> Unit = {}
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
        onClick = {
            if (notification.type == NotificationType.PROMOTION_NEARBY ||
                notification.type == NotificationType.PROMOTION_ENDING) {
                onPromotionClick()
            }
        }
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
                        color = getNotificationColor(notification.type),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getNotificationIcon(notification.type),
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
                        text = getNotificationTitle(notification),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Color(0xFF1E1E1E),
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = getTimeAgo(notification),
                        fontSize = 12.sp,
                        color = Color(0xFF8E8E93)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = getNotificationDescription(notification),
                    fontSize = 14.sp,
                    color = Color(0xFF6D6D6D),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (notification.type == NotificationType.PROMOTION_ENDING) {
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
                            text = "Ends ${notification.promotionData?.endDate?.format(DateTimeFormatter.ofPattern("MMM dd"))}",
                            fontSize = 12.sp,
                            color = Color(0xFF8EC5FF),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (notification.type == NotificationType.SUGGESTED_CARD) {
                    Spacer(modifier = Modifier.height(8.dp))
                    notification.cardData?.perks?.firstOrNull()?.let { perk ->
                        Box(
                            modifier = Modifier
                                .background(
                                    Color(0xFF8EC5FF).copy(alpha = 0.1f),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = perk,
                                fontSize = 12.sp,
                                color = Color(0xFF8EC5FF),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Unread indicator
            if (!notification.isRead) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFF8EC5FF), CircleShape)
                )
            }
        }
    }
}

fun getNotificationIcon(type: NotificationType): ImageVector {
    return when (type) {
        NotificationType.PROMOTION_NEARBY -> Icons.Default.LocationOn
        NotificationType.SUGGESTED_CARD -> Icons.Default.CreditCard
        NotificationType.PROMOTION_ENDING -> Icons.Default.Schedule
    }
}

fun getNotificationColor(type: NotificationType): Color {
    return when (type) {
        NotificationType.PROMOTION_NEARBY -> Color(0xFF8EC5FF)
        NotificationType.SUGGESTED_CARD -> Color(0xFF8EC5FF)
        NotificationType.PROMOTION_ENDING -> Color(0xFF8EC5FF)
    }
}

fun getNotificationTitle(notification: NotificationItem): String {
    return when (notification.type) {
        NotificationType.PROMOTION_NEARBY -> "Promotion Nearby"
        NotificationType.SUGGESTED_CARD -> "Suggested Card"
        NotificationType.PROMOTION_ENDING -> "Last Chance"
    }
}

fun getNotificationDescription(notification: NotificationItem): String {
    return when (notification.type) {
        NotificationType.PROMOTION_NEARBY, NotificationType.PROMOTION_ENDING -> {
            "${notification.promotionData?.name} at ${notification.promotionData?.businessName}"
        }
        NotificationType.SUGGESTED_CARD -> {
            "${notification.cardData?.name} - ${notification.cardData?.description}"
        }
    }
}

fun getTimeAgo(notification: NotificationItem): String {
    return when (notification.type) {
        NotificationType.PROMOTION_NEARBY, NotificationType.PROMOTION_ENDING ->
            notification.promotionData?.timeAgo ?: ""
        NotificationType.SUGGESTED_CARD ->
            notification.cardData?.timeAgo ?: ""
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