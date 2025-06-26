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
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.coded.capstone.R
import com.coded.capstone.navigation.NavRoutes
import com.coded.capstone.viewModels.AccountViewModel
import com.coded.capstone.viewModels.RecommendationViewModel
import com.coded.capstone.ui.AppBackground

@Composable
fun CardSuggestedOnBoarding(
    navController: NavController,
   recommendationViewModel: RecommendationViewModel,
    accountViewModel: AccountViewModel
) {
    var userWillApply by remember { mutableStateOf(false) }
    val recommendedCard by recommendationViewModel.recommendedCard.collectAsState()
    
    // Fetch recommended card when screen is first displayed
    LaunchedEffect(Unit) {
        recommendationViewModel.fetchRecommendedCard()
    }
    
    // Early return with loading state if no card is available
    if (recommendedCard == null) {
        AppBackground {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
        return
    }

    AppBackground {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Your Perfect Account Match",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF374151),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Never Be KLUEless Again",
                    fontSize = 16.sp,
                    color = Color(0xFF6B7280),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 8.dp),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Main Content Container - Takes almost full screen
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(
                        topStart = 50.dp,
                        topEnd = 0.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Card Display - Full width with details below
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .background(
                                        Color.White,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // Card Image - Full width
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(70.dp)
                                            .background(
                                                Color(0xFF1a1a2e),
                                                RoundedCornerShape(6.dp)
                                            )
                                            .padding(6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AsyncImage(
                                            model = recommendedCard?.image ?: "",
                                            contentDescription = "${recommendedCard?.name} card",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(4.dp)),
                                            contentScale = ContentScale.Fit
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Card Details Below
                                    Text(
                                        text = recommendedCard?.name ?: "Recommended Card",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF374151),
                                        textAlign = TextAlign.Center
                                    )

                                    Text(
                                        text = recommendedCard?.accountType ?: "Credit Card",
                                        fontSize = 10.sp,
                                        color = Color(0xFF6B7280),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(top = 1.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Card Details
                            Text(
                                text = recommendedCard?.description ?: "A personalized card recommendation based on your preferences.",
                                fontSize = 12.sp,
                                color = Color(0xFF6B7280),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // Action Buttons
                            Button(
                                onClick = {
                                    userWillApply = true
                                    // Create account with the recommended card
                                    recommendedCard?.id?.let { cardId ->
                                        accountViewModel.createAccount(cardId)
                                    }
                                    navController.navigate(NavRoutes.NAV_ROUTE_HOME) {
                                        popUpTo(0)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF8EC5FF)
                                )
                            ) {
                                Text(
                                    text = "APPLY NOW",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            TextButton(
                                onClick = {
                                    userWillApply = false
                                    // Navigate to home without suggested card (skip)
                                    navController.navigate(NavRoutes.NAV_ROUTE_HOME) {
                                        popUpTo(0)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "continue banking",
                                    color = Color(0xFF6B7280),
                                    fontSize = 10.sp
                                )
                            }


                            // Why This Is Perfect For You Section
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp)
                                ) {
                                    Text(
                                        text = "Why this is perfect for you",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF374151),
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )

                                    recommendedCard?.perks?.forEach { perk ->
                                        Row(
                                            modifier = Modifier.padding(bottom = 4.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = Color(0xFF8EC5FF),
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .offset(y = 1.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = perk.type ?: "Benefit",
                                                fontSize = 10.sp,
                                                color = Color(0xFF6B7280),
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }

                                    if (recommendedCard?.perks?.isNotEmpty() == true) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(
                                                    Color(0xFF8EC5FF).copy(alpha = 0.1f),
                                                    RoundedCornerShape(4.dp)
                                                )
                                                .padding(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Percent,
                                                contentDescription = null,
                                                tint = Color(0xFF8EC5FF),
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Special benefits and rewards included",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF374151)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Single Smart Match Info (without score)
                            if (recommendedCard?.categoryNames?.isNotEmpty() == true) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp)
                                    ) {
                                        Text(
                                            text = "Smart Match",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF374151),
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )

                                        val matchText = buildString {
                                            append("Based on your preferences → ${recommendedCard?.name}")
                                        }

                                        Text(
                                            text = matchText,
                                            fontSize = 9.sp,
                                            color = Color(0xFF6B7280)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            // Progress indicator
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                            ) {
                                repeat(3) { index ->
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .background(
                                                if (index == 2) Color.White else Color.White.copy(alpha = 0.3f),
                                                CircleShape
                                            )
                                    )
                                    if (index < 2) {
                                        Spacer(modifier = Modifier.width(5.dp))
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