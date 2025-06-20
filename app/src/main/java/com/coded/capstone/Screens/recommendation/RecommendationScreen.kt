@file:OptIn(ExperimentalMaterial3Api::class)
package com.coded.capstone.screens.recommendation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coded.capstone.composables.recommendation.RecommendationCard
import com.coded.capstone.data.enums.AccountType
import com.coded.capstone.data.responses.account.AccountProduct
import com.coded.capstone.viewModels.HomeScreenViewModel






@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationScreen(
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    viewModel: HomeScreenViewModel,
    onItemClick: (AccountProduct) -> Unit = {},
    onActivateClick: (AccountProduct) -> Unit = {}
) {
    var expandedItemId by remember { mutableStateOf<String?>(null) }
    val recommendations by viewModel.accountProducts.collectAsState()




    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Draw vertical black block on the right covering the bottom half
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .width(60.dp)
                .height(780.dp)
                .background(Color.Black)
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // White section with horizontal recommendations
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Based on your Accounts",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF222222),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 0.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(recommendations) { recommendation ->
                        Box(
                            modifier = Modifier.width(300.dp)
                        ) {
                            RecommendationCard(
                                item = recommendation,
                                isExpanded = expandedItemId == recommendation.id.toString(),
                                onClick = {
                                    expandedItemId =
                                        if (expandedItemId == recommendation.id.toString()) null else recommendation.id.toString()
                                    onItemClick(recommendation)
                                },
                                onBookClick = { onActivateClick(recommendation) }
                            )
                        }
                    }
                }
            }


        }
    }
}
