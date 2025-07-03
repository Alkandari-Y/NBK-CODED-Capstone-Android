package com.coded.capstone.composables.wallet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.coded.capstone.R
import com.coded.capstone.composables.perks.getPerkColor
import com.coded.capstone.composables.perks.getPerkIcon
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.data.responses.perk.PerkDto
import com.coded.capstone.navigation.NavRoutes

// Roboto font family
private val RobotoFont = FontFamily(
    androidx.compose.ui.text.font.Font(R.font.roboto_variablefont_wdthwght)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerksBottomSheet(
    perks: List<PerkDto>,
    navController: NavController,
    productId: String,
    accountId: String,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
            .background(
                color = Color(0xFF23272E),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .padding(16.dp)
            .padding(bottom = 80.dp)
    ) {
        // Header - MOVED DOWN AND TO THE RIGHT
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, top = 12.dp), // Added left padding and increased top padding
            horizontalArrangement = Arrangement.Start, // Changed from SpaceBetween to Start
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Available Perks",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp, // Slightly smaller
                fontFamily = RobotoFont
            )
        }

        Spacer(modifier = Modifier.height(20.dp)) // Increased spacing

        // Perks Content
        if (perks.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(perks) { index, perk ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(
                            animationSpec = tween(400, delayMillis = index * 100)
                        ) + slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(400, delayMillis = index * 100)
                        )
                    ) {
                        SofterPerkItem(
                            perk = perk,
                            navController = navController,
                            productId = productId,
                            accountId = accountId
                        )
                    }
                }

                // Bottom padding for last item
                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        } else {
            // No perks state
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            Color.White.copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No Perks Available Yet",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    fontFamily = RobotoFont,
                    modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Upgrade your account to unlock exclusive benefits",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Start,
                    fontFamily = RobotoFont,
                    modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun SofterPerkItem(perk: PerkDto, navController: NavController, productId: String, accountId: String, onPerkClick:(String) -> Unit = {}) {
    val isCashback = perk.type?.contains("cashback", ignoreCase = true) == true
    val isDiscount = perk.type?.contains("discount", ignoreCase = true) == true

    // SOFTER COLOR PALETTE - More muted and pleasant
    val perkColor = when {
        isCashback -> Color(0xFF7DD3FC) // Softer sky blue
        isDiscount -> Color(0xFFD8B4FE) // Softer purple
        else -> Color(0xFF86EFAC) // Softer green
    }

    val xpColor = when {
        isCashback -> Color(0xFF0369A1) // Darker blue for contrast
        isDiscount -> Color(0xFF7C3AED) // Darker purple for contrast
        else -> Color(0xFF059669) // Darker green for contrast
    }

    val xpBackgroundColor = when {
        isCashback -> Color(0xFFE0F2FE) // Very light blue
        isDiscount -> Color(0xFFF3E8FF) // Very light purple
        else -> Color(0xFFECFDF5) // Very light green
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp)
            .clickable {
                onPerkClick(perk.id.toString())
                navController.navigate(NavRoutes.relatedVendorRoute(perk.id.toString(), productId, accountId))
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF374151).copy(alpha = 0.8f)), // Softer background
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp), // Increased padding for better spacing
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with softer background
            Box(
                modifier = Modifier
                    .size(48.dp) // Slightly larger
                    .background(perkColor.copy(alpha = 0.2f), CircleShape), // More transparent
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getPerkIcon(perk.type),
                    contentDescription = perk.type,
                    tint = perkColor,
                    modifier = Modifier.size(26.dp) // Slightly larger icon
                )
            }

            Spacer(modifier = Modifier.width(18.dp)) // Increased spacing

            // Perk Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = perk.type?.replaceFirstChar { it.uppercase() } ?: "Perk",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    fontFamily = RobotoFont
                )

                perk.perkAmount?.let { amount ->
                    Text(
                        text = when {
                            isCashback -> "$amount% Back"
                            isDiscount -> "$amount% Off"
                            else -> "$amount Points"
                        },
                        color = perkColor,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        fontFamily = RobotoFont
                    )
                }

                // Categories with softer styling
                if (!perk.categories.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp)) // Increased spacing
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp), // Increased spacing
                        verticalArrangement = Arrangement.spacedBy(6.dp), // Increased spacing
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        perk.categories.forEach { category ->
                            Text(
                                text = category.name ?: "",
                                color = Color(0xFF93C5FD), // Softer blue
                                fontSize = 12.sp,
                                fontFamily = RobotoFont,
                                modifier = Modifier
                                    .background(
                                        color = Color(0xFF1E293B).copy(alpha = 0.6f), // Softer background
                                        shape = RoundedCornerShape(8.dp) // More rounded
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp) // More padding
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }

            // XP Badge with softer styling
            if (perk.rewardsXp != null) {
                Spacer(modifier = Modifier.width(12.dp)) // Increased spacing
                Text(
                    text = "${perk.rewardsXp} XP",
                    color = xpColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    fontFamily = RobotoFont,
                    modifier = Modifier
                        .background(
                            color = xpBackgroundColor,
                            shape = RoundedCornerShape(10.dp) // More rounded
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp) // More padding
                )
            }
        }
    }
}