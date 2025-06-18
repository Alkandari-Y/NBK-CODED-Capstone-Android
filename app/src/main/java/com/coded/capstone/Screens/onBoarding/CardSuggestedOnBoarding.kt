package com.coded.capstone.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.coded.capstone.R

data class NBKCard(
    val id: String,
    val name: String,
    val type: String,
    val description: String,
    val keyBenefits: List<String>,
    val cashbackDetails: String,
    val cardImageResId: Int,
    val backgroundColor: Color,
    val textColor: Color = Color.White
)

@Composable
fun CardSuggestedOnBoarding(
    navController: NavController,
    selectedCategories: Set<String>,
    selectedVendors: Set<String>
) {
    var userWillApply by remember { mutableStateOf(false) }

    // Smart card recommendation algorithm
    val recommendedCard = getRecommendedCard(selectedCategories, selectedVendors)

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
                text = "Your Perfect Account Match",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Never Be KLUEless Again",
                fontSize = 16.sp,
                color = Color(0xFF212937),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Main Content Card
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
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Card Display - Full width with details below
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(
                                Color(0xFFF8FAFC),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Card Image - Full width
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .background(
                                        recommendedCard.backgroundColor,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = recommendedCard.cardImageResId),
                                    contentDescription = "${recommendedCard.name} card",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Card Details Below
                            Text(
                                text = recommendedCard.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2937),
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = recommendedCard.type,
                                fontSize = 14.sp,
                                color = Color(0xFF6B7280),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Card Details
                    Text(
                        text = recommendedCard.description,
                        fontSize = 16.sp,
                        color = Color(0xFF6B7280),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Why This Is Perfect For You Section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF8FAFC)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Why this is perfect for you",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2937),
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            recommendedCard.keyBenefits.forEach { benefit ->
                                Row(
                                    modifier = Modifier.padding(bottom = 8.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier
                                            .size(16.dp)
                                            .offset(y = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = benefit,
                                        fontSize = 14.sp,
                                        color = Color(0xFF374151),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            if (recommendedCard.cashbackDetails.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            Color(0xFF4CAF50).copy(alpha = 0.1f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Percent,
                                        contentDescription = null,
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = recommendedCard.cashbackDetails,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF059669)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Single Smart Match Info (without score)
                    if (selectedCategories.isNotEmpty() || selectedVendors.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFEBF8FF)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Smart Match",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E40AF),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                val matchText = buildString {
                                    if (selectedCategories.isNotEmpty()) {
                                        append("Selected \"${selectedCategories.joinToString("\", \"")}\" categories")
                                    }
                                    if (selectedVendors.isNotEmpty()) {
                                        if (selectedCategories.isNotEmpty()) append(" + ")
                                        append("vendors \"${selectedVendors.joinToString("\", \"")}\"")
                                    }
                                    append(" → ${recommendedCard.name}")
                                }

                                Text(
                                    text = matchText,
                                    fontSize = 13.sp,
                                    color = Color(0xFF1E40AF)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Progress indicator
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    ) {
                        repeat(3) { index ->
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        if (index == 2) Color(0xFF212937) else Color(0xFFE5E7EB),
                                        CircleShape
                                    )
                            )
                            if (index < 2) {
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                    }

                    // Action Buttons
                    Button(
                        onClick = {
                            userWillApply = true
                            // Navigate to home with suggested card NAME (not ID)
                            navController.navigate("home/${recommendedCard.name}") {
                                popUpTo(0)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF212937)
                        )
                    ) {
                        Text(
                            text = "APPLY NOW",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = {
                            userWillApply = false
                            // Navigate to home without suggested card (skip)
                            navController.navigate("home") {
                                popUpTo(0)
                            }
                        }
                    ) {
                        Text(
                            text = "continue banking",
                            color = Color(0xFF6B7280),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

// Smart card recommendation algorithm
fun getRecommendedCard(selectedCategories: Set<String>, selectedVendors: Set<String>): NBKCard {
    val allCards = getAllNBKCards()

    // Score each card based on user preferences
    return allCards.maxByOrNull { card ->
        calculateCardScore(card, selectedCategories, selectedVendors)
    } ?: allCards.first() // Default fallback
}

fun calculateCardScore(card: NBKCard, categories: Set<String>, vendors: Set<String>): Int {
    var score = 0

    when (card.id) {
        "nbk_kwt_infinite" -> {
            if (categories.contains("dining")) score += 50 // Perfect for dining with 10% points
            if (categories.contains("technology")) score += 40 // Great for X-cite
            if (vendors.contains("xcite")) score += 30
            if (vendors.contains("pret") || vendors.contains("shakeshack")) score += 25
        }
        "nbk_aura_world" -> {
            if (categories.contains("shopping")) score += 50 // Perfect for shopping with 8% Aura points
            if (vendors.contains("hm") || vendors.contains("theavenues")) score += 30
            if (vendors.contains("bathandbody") || vendors.contains("harveynichols")) score += 25
        }
        "nbk_miles_world" -> {
            if (categories.contains("travel")) score += 50 // Perfect for travel with 5 miles/KD
            if (vendors.contains("kuwaitairways")) score += 30
            if (vendors.contains("jumeirah") || vendors.contains("booking")) score += 25
        }
        "nbk_kuwait_airways_infinite" -> {
            if (categories.contains("travel")) score += 45 // Great for travel
            if (vendors.contains("kuwaitairways")) score += 35 // Perfect for Kuwait Airways users
        }
        "nbk_visa_infinite" -> {
            if (categories.contains("lifestyle")) score += 40 // Ultra-premium lifestyle
            score += categories.size * 20 // Bonus for multiple categories
        }
        "nbk_visa_platinum" -> {
            // General purpose card - moderate scores for all categories
            score += categories.size * 15
            if (vendors.contains("vox")) score += 20 // VOX 50% off
        }
    }

    return score
}

fun getAllNBKCards(): List<NBKCard> {
    return listOf(
        NBKCard(
            id = "nbk_kwt_infinite",
            name = "NBK KWT Visa Infinite",
            type = "Premium Credit Card",
            description = "Kuwait's premier card designed for nationals who value local benefits and premium lifestyle.",
            keyBenefits = listOf(
                "Perfect match for your dining preferences with up to 10% NBK KWT Points",
                "Exclusive benefits at X-cite Electronics with 2% instant discount + 10% points",
                "Access to Kuwait's largest loyalty program with 900+ outlets",
                "Premium Visa Infinite benefits and luxury hotel collection access"
            ),
            cashbackDetails = "Up to 10% in NBK KWT Points on dining and telecom",
            cardImageResId = R.drawable.infinite,
            backgroundColor = Color(0xFF1a1a2e)
        ),
        NBKCard(
            id = "nbk_aura_world",
            name = "NBK-Aura World Mastercard",
            type = "Shopping Rewards Card",
            description = "The ultimate shopping companion for fashion and lifestyle enthusiasts across Kuwait's top retail destinations.",
            keyBenefits = listOf(
                "Perfect for your shopping interests with up to 8% back in Aura points",
                "Exclusive access to 50+ Alshaya brands including H&M and Bath & Body Works",
                "4% back in points on all purchases at The Avenues",
                "World Mastercard premium benefits and global acceptance"
            ),
            cashbackDetails = "Up to 8% back in Aura Points at participating outlets",
            cardImageResId = R.drawable.aura,
            backgroundColor = Color(0xFFec4899)
        ),
        NBKCard(
            id = "nbk_miles_world",
            name = "NBK Miles World Mastercard",
            type = "Travel Rewards Card",
            description = "Designed for travelers who want to turn every purchase into their next adventure with premium travel benefits.",
            keyBenefits = listOf(
                "Ideal for your travel interests with 5 NBK Miles Points per KD internationally",
                "12 complimentary airport lounge visits annually at 1,200+ global lounges",
                "Exclusive Kuwait Airways partnership with 10% discount + 4 Miles/KD",
                "Travel insurance and premium travel concierge services"
            ),
            cashbackDetails = "5 Miles Points per KD internationally, 3 points locally",
            cardImageResId = R.drawable.miles,
            backgroundColor = Color(0xFF3b82f6)
        ),
        NBKCard(
            id = "nbk_visa_infinite",
            name = "NBK Visa Infinite",
            type = "Ultra-Premium Credit Card",
            description = "The ultimate banking experience for discerning customers who demand the finest in premium benefits.",
            keyBenefits = listOf(
                "Ultra-premium Visa Infinite benefits and global recognition",
                "Exclusive concierge services and luxury lifestyle benefits",
                "Premium travel insurance and global lounge access",
                "NBK Rewards Program with enhanced earning rates"
            ),
            cashbackDetails = "Premium NBK Rewards Points with enhanced benefits",
            cardImageResId = R.drawable.visa_infinite,
            backgroundColor = Color(0xFF1a1a2e)
        ),
        NBKCard(
            id = "nbk_visa_platinum",
            name = "NBK Visa Platinum",
            type = "Premium Credit Card",
            description = "A versatile premium card offering excellent benefits across all lifestyle categories with global acceptance.",
            keyBenefits = listOf(
                "Balanced benefits across all your selected interests",
                "50% off VOX Cinemas tickets when purchased online",
                "6 complimentary airport lounge visits annually",
                "NBK Rewards Program access to 900+ partner outlets"
            ),
            cashbackDetails = "NBK Rewards Points at 900+ outlets",
            cardImageResId = R.drawable.visa_platinum,
            backgroundColor = Color(0xFF6b7280)
        ),
        NBKCard(
            id = "nbk_kuwait_airways_infinite",
            name = "NBK-Kuwait Airways Infinite",
            type = "Co-branded Travel Card",
            description = "The ultimate travel companion with exclusive Kuwait Airways benefits and premium travel rewards.",
            keyBenefits = listOf(
                "Perfect for frequent Kuwait Airways travelers",
                "Earn 4 Oasis Club Miles per KD internationally",
                "10% discount on all Kuwait Airways tickets",
                "Unlimited lounge access and priority services"
            ),
            cashbackDetails = "4 Oasis Club Miles per KD + 10% ticket discount",
            cardImageResId = R.drawable.kuwait_airways_infinite,
            backgroundColor = Color(0xFF2563eb)
        )
    )
}