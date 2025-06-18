package com.coded.capstone.screens.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.coded.capstone.R

data class MerchantPartner(
    val id: String,
    val name: String,
    val category: String,
    val offer: String,
    val logoResId: Int?, // null for category icon fallback
    val eligibleCards: String,
    val isPopular: Boolean = false
)

// Category icon mapping
fun getCategoryIcon(category: String): ImageVector = when (category) {
    "dining" -> Icons.Default.Restaurant
    "travel" -> Icons.Default.Flight
    "shopping" -> Icons.Default.ShoppingBag
    "technology" -> Icons.Default.Smartphone
    "lifestyle" -> Icons.Default.Home
    "entertainment" -> Icons.Default.Movie
    else -> Icons.Default.Store
}

@Composable
fun VendorsOnBoarding(
    navController: NavController,
    selectedCategories: Set<String>
) {
    var selectedVendors by remember { mutableStateOf(setOf<String>()) }
    var searchQuery by remember { mutableStateOf("") }

    // Real NBK merchant partners matching categories
    val vendors = listOf(
        // DINING PARTNERS
        MerchantPartner(
            id = "pret",
            name = "Pret A Manger",
            category = "dining",
            offer = "Buy 1 Get 1 Free",
            logoResId = R.drawable.ic_pret_a_manger,
            eligibleCards = "All NBK Cards",
            isPopular = true
        ),
        MerchantPartner(
            id = "shakeshack",
            name = "Shake Shack",
            category = "dining",
            offer = "NBK Rewards Points",
            logoResId = R.drawable.ic_shake_shack,
            eligibleCards = "NBK Credit Cards",
            isPopular = true
        ),
        MerchantPartner(
            id = "caribou",
            name = "Caribou Coffee",
            category = "dining",
            offer = "Buy 1 Get 1 Free",
            logoResId = R.drawable.ic_caribou_coffee,
            eligibleCards = "All NBK Cards",
            isPopular = true
        ),
        MerchantPartner(
            id = "illy",
            name = "illy Caffè",
            category = "dining",
            offer = "Buy 1 Get 1 Free",
            logoResId = R.drawable.ic_illy_caffe,
            eligibleCards = "All NBK Cards"
        ),
        MerchantPartner(
            id = "roka",
            name = "ROKA Kuwait",
            category = "dining",
            offer = "20% Off",
            logoResId = R.drawable.ic_roka_kuwait,
            eligibleCards = "NBK World Elite"
        ),

        // TECHNOLOGY PARTNERS
        MerchantPartner(
            id = "xcite",
            name = "X-cite",
            category = "technology",
            offer = "2% Instant + 10% Points",
            logoResId = R.drawable.ic_xcite,
            eligibleCards = "NBK KWT Visa",
            isPopular = true
        ),

        // SHOPPING PARTNERS
        MerchantPartner(
            id = "hm",
            name = "H&M",
            category = "shopping",
            offer = "Up to 8% Aura Points",
            logoResId = R.drawable.ic_hm,
            eligibleCards = "NBK-Aura Card"
        ),
        MerchantPartner(
            id = "bathandbody",
            name = "Bath & Body Works",
            category = "shopping",
            offer = "Up to 8% Aura Points",
            logoResId = R.drawable.ic_bath_body_works,
            eligibleCards = "NBK-Aura Card"
        ),
        MerchantPartner(
            id = "harveynichols",
            name = "Harvey Nichols",
            category = "shopping",
            offer = "Up to 8% Aura Points",
            logoResId = R.drawable.ic_harvey_nichols,
            eligibleCards = "NBK-Aura Card"
        ),
        MerchantPartner(
            id = "footlocker",
            name = "Footlocker",
            category = "shopping",
            offer = "Up to 8% Aura Points",
            logoResId = R.drawable.ic_footlocker,
            eligibleCards = "NBK-Aura Card"
        ),
        MerchantPartner(
            id = "farfetch",
            name = "FARFETCH",
            category = "shopping",
            offer = "10% NBK KWT Points",
            logoResId = R.drawable.ic_farfetch,
            eligibleCards = "NBK KWT Visa"
        ),
        MerchantPartner(
            id = "theavenues",
            name = "The Avenues",
            category = "shopping",
            offer = "4% NBK KWT Points",
            logoResId = R.drawable.ic_the_avenues,
            eligibleCards = "NBK KWT Cards",
            isPopular = true
        ),

        // ENTERTAINMENT PARTNERS
        MerchantPartner(
            id = "vox",
            name = "VOX Cinemas",
            category = "entertainment",
            offer = "50% Off Online",
            logoResId = R.drawable.ic_vox_cinemas,
            eligibleCards = "NBK Visa Signature",
            isPopular = true
        ),
        MerchantPartner(
            id = "anghami",
            name = "Anghami Plus",
            category = "entertainment",
            offer = "4 months free",
            logoResId = R.drawable.ic_anghami,
            eligibleCards = "NBK Visa Platinum"
        ),
        MerchantPartner(
            id = "cinescape",
            name = "Cinescape",
            category = "entertainment",
            offer = "Buy 1 Get 1 Free",
            logoResId = R.drawable.ic_cinescape,
            eligibleCards = "NBK KWT Cards"
        ),

        // LIFESTYLE PARTNERS
        MerchantPartner(
            id = "ikea",
            name = "IKEA Kuwait",
            category = "lifestyle",
            offer = "Free delivery + 10% dining",
            logoResId = R.drawable.ic_ikea,
            eligibleCards = "All NBK Cards",
            isPopular = true
        ),
        MerchantPartner(
            id = "charlottetilbury",
            name = "Charlotte Tilbury",
            category = "lifestyle",
            offer = "Up to 8% Aura Points",
            logoResId = R.drawable.ic_charlotte_tilbury,
            eligibleCards = "NBK-Aura Card"
        ),

        // TRAVEL PARTNERS
        MerchantPartner(
            id = "kuwaitairways",
            name = "Kuwait Airways",
            category = "travel",
            offer = "10% discount + 4 Miles/KD",
            logoResId = R.drawable.ic_kuwait_airways,
            eligibleCards = "NBK-Kuwait Airways",
            isPopular = true
        ),
        MerchantPartner(
            id = "jumeirah",
            name = "Jumeirah Hotels",
            category = "travel",
            offer = "Up to 25% Off",
            logoResId = R.drawable.ic_jumeirah_hotels,
            eligibleCards = "NBK Miles Card"
        ),
        MerchantPartner(
            id = "booking",
            name = "Booking.com",
            category = "travel",
            offer = "Up to 8% instant discount",
            logoResId = R.drawable.ic_booking_com,
            eligibleCards = "NBK Visa Credit"
        )
    )

    val filteredVendors = if (searchQuery.isBlank()) {
        vendors.sortedWith(compareBy<MerchantPartner> { vendor ->
            when {
                selectedCategories.contains(vendor.category) && vendor.isPopular -> 0
                selectedCategories.contains(vendor.category) && !vendor.isPopular -> 1
                !selectedCategories.contains(vendor.category) && vendor.isPopular -> 2
                else -> 3
            }
        }.thenBy { it.name })
    } else {
        vendors.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.category.contains(searchQuery, ignoreCase = true)
        }.sortedByDescending { it.isPopular }
    }

    fun toggleVendor(vendorId: String) {
        if (selectedVendors.contains(vendorId)) {
            selectedVendors = selectedVendors - vendorId
        } else {
            selectedVendors = selectedVendors + vendorId
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE0E0E0),
                        Color(0xFF212937).copy(alpha = 0.05f),
                        Color(0xFF212937).copy(alpha = 0.1f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Logo
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        Color(0xFF081538),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "KLUE",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Personalize Your Banking",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )

            Text(
                text = "Never Be KLUEless Again",
                fontSize = 16.sp,
                color = Color(0xFF212937),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Content Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    // Header
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(bottom = 20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Store,
                            contentDescription = null,
                            tint = Color(0xFF20436C),
                            modifier = Modifier
                                .size(48.dp)
                                .padding(bottom = 16.dp)
                        )

                        Text(
                            text = "Select your favorite vendors",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Personalized offers from Kuwait's best merchants",
                            fontSize = 16.sp,
                            color = Color(0xFF6B7280),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
                        )

                        Text(
                            text = "Based on your interests: ${selectedCategories.joinToString(", ")}",
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Text(
                            text = "${selectedVendors.size} vendors selected",
                            fontSize = 14.sp,
                            color = Color(0xFF212937),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Text(
                            text = "${selectedVendors.size} vendors selected",
                            fontSize = 14.sp,
                            color = Color(0xFF212937),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search vendors, categories...") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = Color(0xFF6B7280)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF212937),
                            unfocusedBorderColor = Color(0xFFE5E7EB)
                        )
                    )

                    // Vendors Scrollable Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(filteredVendors) { vendor ->
                            NBKVendorCard(
                                vendor = vendor,
                                isSelected = selectedVendors.contains(vendor.id),
                                onClick = { toggleVendor(vendor.id) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        repeat(3) { index ->
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        if (index == 1) Color(0xFF212937) else Color(0xFFE5E7EB),
                                        CircleShape
                                    )
                            )
                            if (index < 2) {
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                    }

                    // Navigation Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(
                            onClick = {
                                val categoriesString = selectedCategories.joinToString(",")
                                val vendorsString = selectedVendors.joinToString(",")
                                navController.navigate("card_suggested_onboarding/$categoriesString/$vendorsString")
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color(0xFF6B7280)
                            )
                        ) {
                            Text("Skip this step")
                        }

                        Button(
                            onClick = {
                                val categoriesString = selectedCategories.joinToString(",")
                                val vendorsString = selectedVendors.joinToString(",")
                                navController.navigate("card_suggested_onboarding/$categoriesString/$vendorsString")
                            },
                            modifier = Modifier
                                .height(56.dp)
                                .widthIn(min = 120.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF212937)
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "NEXT",
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
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
fun NBKVendorCard(
    vendor: MerchantPartner,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "vendor_scale"
    )

    // Check if resource exists
    val logoExists = vendor.logoResId?.let { resId ->
        try {
            context.resources.getDrawable(resId, null)
            true
        } catch (e: Exception) {
            false
        }
    } ?: false

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .scale(scale),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            2.dp,
            if (isSelected) Color(0xFF4CAF50) else Color(0xFFE5E7EB)
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                Color(0xFF4CAF50).copy(alpha = 0.08f)
            } else {
                Color.White
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Popular badge
            if (vendor.isPopular) {
                Box(
                    modifier = Modifier
                        .background(
                            Color(0xFFF59E0B),
                            RoundedCornerShape(bottomEnd = 8.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = "Popular",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Selection indicator
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            Color(0xFF4CAF50),
                            CircleShape
                        )
                        .align(Alignment.TopEnd)
                        .offset((-8).dp, 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Logo - use image if available and exists, fallback to category icon
                if (logoExists && vendor.logoResId != null) {
                    Image(
                        painter = painterResource(id = vendor.logoResId),
                        contentDescription = "${vendor.name} logo",
                        modifier = Modifier
                            .size(35.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    // Fallback to category icon
                    Icon(
                        imageVector = getCategoryIcon(vendor.category),
                        contentDescription = "${vendor.category} category",
                        tint = Color(0xFF03A9F4),
                        modifier = Modifier
                            .size(35.dp)
                            .padding(bottom = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = vendor.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = vendor.offer,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                Text(
                    text = vendor.eligibleCards,
                    fontSize = 10.sp,
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}