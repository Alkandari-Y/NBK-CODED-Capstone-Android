package com.coded.capstone.composables.recommendation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.coded.capstone.data.responses.accountProduct.AccountProductResponse
import com.coded.capstone.composables.wallet.WalletCard
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.SVG.SharpStarsIcon
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RecommendationCard(
    item: AccountProductResponse?,
    onClick: () -> Unit,
    onBookClick: () -> Unit,
    isLoading: Boolean = false,
    recommendationType: String? = null
) {
    var showCategoriesModal by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
    // Function to get categories based on product
    fun getCategories(product: AccountProductResponse?): List<String> {
        return when {
            product?.name?.lowercase()?.contains("travel") == true -> listOf("Travel", "Hotels", "Flights", "Car Rental")
            product?.name?.lowercase()?.contains("family") == true -> listOf("Family", "Education", "Healthcare", "Shopping")
            product?.name?.lowercase()?.contains("entertainment") == true -> listOf("Movies", "Streaming", "Gaming", "Events")
            product?.name?.lowercase()?.contains("shopping") == true -> listOf("Online Shopping", "Retail", "Fashion", "Electronics")
            product?.name?.lowercase()?.contains("dining") == true -> listOf("Restaurants", "Food Delivery", "Cafes", "Bars")
            product?.name?.lowercase()?.contains("health") == true -> listOf("Healthcare", "Pharmacy", "Fitness", "Wellness")
            product?.name?.lowercase()?.contains("education") == true -> listOf("Education", "Books", "Courses", "Software")
            product?.accountType?.lowercase() == "credit" -> listOf("Shopping", "Travel", "Dining", "Entertainment")
            product?.accountType?.lowercase() == "savings" -> listOf("Family", "Education", "Healthcare", "Future Planning")
            product?.accountType?.lowercase() == "debit" -> listOf("Daily Expenses", "Shopping", "Dining", "Transport")
            else -> listOf("General", "Shopping", "Dining", "Entertainment")
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color(0xFF6366F1).copy(alpha = 0.3f),
                spotColor = Color(0xFF8B5CF6).copy(alpha = 0.3f)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background with stunning gradient
            if (item?.image != null) {
                AsyncImage(
                    model = item.image,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Glassmorphism overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF6366F1).copy(alpha = 0.4f),
                                    Color(0xFF8B5CF6).copy(alpha = 0.6f),
                                    Color(0xFF1E1B4B).copy(alpha = 0.8f)
                                ),
                                radius = 800f
                            )
                        )
                )
            } else {
                // Premium gradient background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF667EEA),
                                    Color(0xFF764BA2),
                                    Color(0xFF667EEA)
                                ),
                                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                                end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
                            )
                        )
                )
            }

            // Premium glassmorphism overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.1f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.3f)
                            )
                        )
                    )
            )

            // Content Layer
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Header Section with enhanced styling
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item?.accountType?.uppercase() ?: "PREMIUM ACCOUNT",
                            fontSize = 11.sp,
                            color = Color(0xFFE2E8F0),
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.2.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item?.name ?: "Premium Banking Product",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 24.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.3f),
                                        Color.White.copy(alpha = 0.1f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Diamond,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Rotated Recommendation Card Display
                if (recommendationType != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        RecommendationCardDisplay(
                            product = item,
                            recommendationType = recommendationType,
                            modifier = Modifier
                                .width(220.dp)
                                .height(120.dp)
                                .graphicsLayer {
                                    rotationZ = 90f
                                    scaleX = 0.8f
                                    scaleY = 0.8f
                                }
                                .shadow(
                                    elevation = 16.dp,
                                    shape = RoundedCornerShape(20.dp),
                                    ambientColor = Color.Black.copy(alpha = 0.3f),
                                    spotColor = Color.Black.copy(alpha = 0.3f)
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Premium Info Cards
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, end = 4.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item?.interestRate?.let {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Interest Rate", color = Color(0xFF1E1B4B), fontWeight = FontWeight.Medium, fontSize = 13.sp)
                            Text("${it}%", color = Color(0xFF1E1B4B), fontWeight = FontWeight.Medium, fontSize = 13.sp)
                        }
                    }
                    item?.creditLimit?.let {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Credit Limit", color = Color(0xFF1E1B4B), fontWeight = FontWeight.Medium, fontSize = 13.sp)
                            Text("KD ${it.toInt()}", color = Color(0xFF1E1B4B), fontWeight = FontWeight.Medium, fontSize = 13.sp)
                        }
                    }
                    item?.annualFee?.let {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Annual Fee", color = Color(0xFF1E1B4B), fontWeight = FontWeight.Medium, fontSize = 13.sp)
                            Text(if (it == 0.0) "Free" else "KD ${it.toInt()}", color = Color(0xFF1E1B4B), fontWeight = FontWeight.Medium, fontSize = 13.sp)
                        }
                    }
                    item?.minBalanceRequired?.let {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Min Balance", color = Color(0xFF1E1B4B), fontWeight = FontWeight.Medium, fontSize = 13.sp)
                            Text("KD ${it.toInt()}", color = Color(0xFF1E1B4B), fontWeight = FontWeight.Medium, fontSize = 13.sp)
                        }
                    }
                    item?.minSalary?.let {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Min Salary", color = Color(0xFF1E1B4B), fontWeight = FontWeight.Medium, fontSize = 13.sp)
                            Text("KD ${it.toInt()}", color = Color(0xFF1E1B4B), fontWeight = FontWeight.Medium, fontSize = 13.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Premium Apply Button
                Button(
                    onClick = onBookClick,
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(16.dp),
                            ambientColor = Color.White.copy(alpha = 0.3f)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF1E1B4B),
                        disabledContainerColor = Color.White.copy(alpha = 0.6f),
                        disabledContentColor = Color(0xFF1E1B4B).copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color(0xFF1E1B4B),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Creating Account...",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                text = "Apply Now",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
            
            // Star Button - Top Right (positioned above all content)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
                    .zIndex(10f)
            ) {
                IconButton(
                    onClick = { showCategoriesModal = true },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.3f),
                                    Color.White.copy(alpha = 0.1f)
                                )
                            )
                        )
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            ambientColor = Color.Black.copy(alpha = 0.3f)
                        )
                ) {
                    SharpStarsIcon(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                }
            }
        }
    }
    
    // Categories Modal
    if (showCategoriesModal) {
        Dialog(
            onDismissRequest = { showCategoriesModal = false },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF23272E)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Card Categories",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        IconButton(
                            onClick = { showCategoriesModal = false }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Categories Grid
                    val categories = getCategories(item)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(categories.size) { index ->
                            val category = categories[index]
                            Card(
                                modifier = Modifier
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF8EC5FF).copy(alpha = 0.2f)
                                )
                            ) {
                                Text(
                                    text = category,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Description
                    Text(
                        text = "This card offers rewards and benefits in these categories",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun InfoCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    isSecondary: Boolean = false,
    cardColor: Color = Color.White,
    valueFontSize: TextUnit = 16.sp,
    labelFontSize: TextUnit = 10.sp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
//            .background(cardColor)
            .padding(5.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = value,
                fontSize = valueFontSize,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = labelFontSize,
                color = Color.DarkGray.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
private fun RecommendationCardDisplay(
    product: AccountProductResponse?,
    recommendationType: String,
    modifier: Modifier = Modifier
) {
    // Get recommendation-specific styling
    val cardGradient = when (recommendationType.lowercase()) {
        "travel" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF7AF380), // Light green
                Color(0xFFA5F5A9), // Lighter green
                Color(0xFF136870), // Dark teal
                Color(0xFF136870)  // Dark teal
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
        "family essentials" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF49899F), // Blue-gray
                Color(0xFF80D1EC), // Light blue
                Color(0xFF115F79), // Dark blue
                Color(0xFF115F79)  // Dark blue
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
        "entertainment" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF651351), // Dark purple
                Color(0xFF8D3077), // Purple
                Color(0xFF2C1365), // Darker purple
                Color(0xFF2C1365)  // Darker purple
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
        "shopping" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFDC2626), // Red 600
                Color(0xFFEF4444), // Red 500
                Color(0xFFB91C1C), // Red 700
                Color(0xFFDC2626)  // Red 600
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
        "dining" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFD97706), // Amber 600
                Color(0xFFF59E0B), // Amber 500
                Color(0xFFB45309), // Amber 700
                Color(0xFFD97706)  // Amber 600
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
        "health" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF0891B2), // Cyan 600
                Color(0xFF06B6D4), // Cyan 500
                Color(0xFF0E7490), // Cyan 700
                Color(0xFF0891B2)  // Cyan 600
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
        "education" -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF7C2D12), // Orange 800
                Color(0xFFEA580C), // Orange 600
                Color(0xFF9A3412), // Orange 700
                Color(0xFF7C2D12)  // Orange 800
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
        else -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF384349), // Gray 700
                Color(0xFF58656C), // Gray 600
                Color(0xFF273034), // Gray 500
                Color(0xFF384349)  // Gray 400
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(350f, 250f)
        )
    }

    // Get recommendation icon
    val recommendationIcon = when (recommendationType.lowercase()) {
        "travel" -> Icons.Default.Flight
        "family essentials" -> Icons.Default.FamilyRestroom
        "entertainment" -> Icons.Default.Movie
        "shopping" -> Icons.Default.ShoppingCart
        "dining" -> Icons.Default.Restaurant
        "health" -> Icons.Default.LocalHospital
        "education" -> Icons.Default.School
        else -> Icons.Default.CreditCard
    }

    // Get recommendation label
    val recommendationLabel = when (recommendationType.lowercase()) {
        "travel" -> "TRAVEL"
        "family essentials" -> "FAMILY"
        "entertainment" -> "ENTERTAINMENT"
        "shopping" -> "SHOPPING"
        "dining" -> "DINING"
        "health" -> "HEALTH"
        "education" -> "EDUCATION"
        else -> "RECOMMENDED"
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(cardGradient)
        ) {
            // Subtle geometric pattern overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.03f),
                                Color.Transparent,
                                Color.White.copy(alpha = 0.02f)
                            ),
                            radius = 400f
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top section: Bank name and recommendation icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Bank/Product name
                    Text(
                        text = (product?.name ?: "PREMIUM").uppercase(),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )

                    // Recommendation icon
                    Icon(
                        imageVector = recommendationIcon,
                        contentDescription = recommendationLabel,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Middle section: EMV Chip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    // Realistic EMV Chip
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(32.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFFFD700), // Gold
                                        Color(0xFFDAA520), // Goldenrod
                                        Color(0xFFB8860B), // Dark goldenrod
                                        Color(0xFFFFD700)  // Gold
                                    )
                                ),
                                RoundedCornerShape(4.dp)
                            )
                            .shadow(1.dp, RoundedCornerShape(4.dp))
                    ) {
                        // Chip contact pattern
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(6.dp),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            repeat(2) { row ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    repeat(3) { col ->
                                        Box(
                                            modifier = Modifier
                                                .size(2.dp)
                                                .background(
                                                    Color(0xFF8B4513).copy(alpha = 0.8f),
                                                    RoundedCornerShape(1.dp)
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Bottom section: Card details
                Column {
                    // Product type
                    Text(
                        text = "•••• •••• •••• ••••",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Bottom row: Recommendation type and product info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // Recommendation type
                        Column {
                            Text(
                                text = "RECOMMENDATION",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = recommendationLabel,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        }

                        // Product type
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "PRODUCT",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = product?.accountType?.uppercase() ?: "PREMIUM",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Premium shine effect
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.12f),
                                Color.Transparent,
                                Color.White.copy(alpha = 0.08f),
                                Color.Transparent,
                                Color.White.copy(alpha = 0.15f),
                                Color.Transparent
                            ),
                            start = androidx.compose.ui.geometry.Offset(0f, 0f),
                            end = androidx.compose.ui.geometry.Offset(500f, 300f)
                        )
                    )
            )
        }
    }
}