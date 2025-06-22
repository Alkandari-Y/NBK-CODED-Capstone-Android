package com.coded.capstone.composables.wallet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.data.responses.perk.PerkDto

//@Composable
//fun PerksBottomSheet(
//    account: AccountResponse,
//    perks: List<PerkDto>,
//    expanded: Boolean,
//    onExpandedChange: (Boolean) -> Unit,
//    onDismiss: () -> Unit
//) {
//    Box(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        Card(
//            modifier = Modifier
//                .fillMaxWidth()
//                .fillMaxHeight(if (expanded) 1f else 0.9f) // Reduced heights
//                .align(Alignment.BottomCenter),
//            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
//            shape = RoundedCornerShape(
//                topStart = 70.dp,
//                topEnd = 0.dp,
//                bottomStart = 0.dp,
//                bottomEnd = 0.dp
//            ),
//            colors = CardDefaults.cardColors(containerColor = Color.White)
//        ) {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 32.dp, vertical = 40.dp)
//            ) {
//                // Header with close and expand buttons
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = "Account Perks",
//                        style = MaterialTheme.typography.headlineSmall.copy(
//                            fontWeight = FontWeight.Bold,
//                            fontSize = 28.sp
//                        ),
//                        color = Color.Black
//                    )
//
//                    Row {
//                        IconButton(
//                            onClick = { onExpandedChange(!expanded) },
//                            modifier = Modifier
//                                .size(40.dp)
//                                .background(
//                                    Color(0xFFF8F8F8),
//                                    CircleShape
//                                )
//                        ) {
//                            Icon(
//                                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
//                                contentDescription = if (expanded) "Collapse" else "Expand",
//                                tint = Color.Black,
//                                modifier = Modifier.size(20.dp)
//                            )
//                        }
//
//                        Spacer(modifier = Modifier.width(8.dp))
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(32.dp))
//
//                // Perks List
//                if (perks.isNotEmpty()) {
//                    Text(
//                        text = "✨ Your Benefits",
//                        color = Color.Black,
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 20.sp,
//                        modifier = Modifier.padding(bottom = 16.dp)
//                    )
//
//                    LazyColumn(
//                        modifier = Modifier.fillMaxHeight(),
//                        verticalArrangement = Arrangement.spacedBy(16.dp)
//                    ) {
//                        itemsIndexed(perks) { index, perk ->
//                            AnimatedVisibility(
//                                visible = true,
//                                enter = fadeIn(
//                                    animationSpec = tween(400, delayMillis = index * 100)
//                                ) + slideInHorizontally(
//                                    initialOffsetX = { it },
//                                    animationSpec = tween(400, delayMillis = index * 100)
//                                )
//                            ) {
//                                EnhancedPerkItem(perk = perk)
//                            }
//                        }
//                    }
//                } else {
//                    // No perks state with clean design
//                    Column(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        Box(
//                            modifier = Modifier
//                                .size(120.dp)
//                                .background(
//                                    Color(0xFFF8F8F8),
//                                    shape = CircleShape
//                                ),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.Star,
//                                contentDescription = null,
//                                tint = Color(0xFF757575),
//                                modifier = Modifier.size(48.dp)
//                            )
//                        }
//                        Spacer(modifier = Modifier.height(24.dp))
//                        Text(
//                            text = "No Perks Available Yet",
//                            color = Color.Black,
//                            fontWeight = FontWeight.Bold,
//                            fontSize = 24.sp
//                        )
//
//                        Spacer(modifier = Modifier.height(24.dp))
//                    }
//                }
//            }
//        }
//    }
//}