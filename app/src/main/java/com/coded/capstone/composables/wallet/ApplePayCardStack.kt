package com.coded.capstone.composables.wallet

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex
import androidx.compose.ui.unit.dp
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.respositories.AccountProductRepository

@Composable
fun ApplePayCardStack(
    accounts: List<AccountResponse>,
    selectedCard: AccountResponse?,
    pagerState: PagerState,
    scrollVelocity: Float,
    onCardSelected: (AccountResponse) -> Unit,
    onScrollVelocityChange: (Float) -> Unit,
    externalExpandTrigger: Boolean = false
) {
    // Expansion state for card stack
    var isExpanded by remember { mutableStateOf(false) }

    // Trigger expansion from external source
    LaunchedEffect(externalExpandTrigger) {
        if (externalExpandTrigger) {
            isExpanded = !isExpanded // Toggle expansion state
        }
    }

    val expansionOffset by animateFloatAsState(
        targetValue = if (isExpanded) 80f else 0f,
        animationSpec = spring(
            dampingRatio = 0.8f,
            stiffness = 200f
        ),
        label = "expansionOffset"
    )

    // Function to get unified recommendation type - SAME LOGIC AS WALLETCARD
    fun getRecommendationType(account: AccountResponse): String? {
        val accountProduct = AccountProductRepository.accountProducts.find {
            it.id == account.accountProductId
        }

        val bankName = accountProduct?.name ?: ""

        return when {
            // Specific product names first
            bankName.lowercase().contains("cashback") -> "retail"
            bankName.lowercase().contains("shopping") -> "retail"
            bankName.lowercase().contains("diamond") -> "fashion"
            bankName.lowercase().contains("platinum") -> "wholesale"
            bankName.lowercase().contains("salary") -> "education"
            bankName.lowercase().contains("business pro") -> "technology"
            bankName.lowercase().contains("youth starter") -> "entertainment"
            bankName.lowercase().contains("shopper's delight") -> "retail"
            bankName.lowercase().contains("lifestyle premium") -> "fashion"

            // General category names
            bankName.lowercase().contains("retail") -> "retail"
            bankName.lowercase().contains("travel") -> "travel"
            bankName.lowercase().contains("dining") -> "dining"
            bankName.lowercase().contains("fashion") -> "fashion"
            bankName.lowercase().contains("technology") -> "technology"
            bankName.lowercase().contains("hospitality") -> "hospitality"
            bankName.lowercase().contains("education") -> "education"
            bankName.lowercase().contains("entertainment") -> "entertainment"
            bankName.lowercase().contains("personal care") -> "personal care"
            bankName.lowercase().contains("wholesale") -> "wholesale"

            // Fallback based on account type
            account.accountType?.lowercase() == "credit" -> "retail"
            account.accountType?.lowercase() == "savings" -> "hospitality"
            account.accountType?.lowercase() == "debit" -> "travel"
            account.accountType?.lowercase() == "business" -> "technology"
            else -> "retail" // Default recommendation type
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp)
            .clickable {
                // Expand stack when tapping on the container
                if (!isExpanded) {
                    isExpanded = true
                }
            },
        contentAlignment = Alignment.TopCenter
    ) {
        accounts.forEachIndexed { index, account ->
            val baseOffset = (index * (48 + expansionOffset)).dp
            val scale = 0.98f - (index * 0.02f)
            val alpha = 1f // All cards have full opacity

            // Get recommendation type for this account
            val recommendationType = getRecommendationType(account)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .offset(y = baseOffset)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    }
                    .zIndex(1000f - index)
                    .shadow(
                        elevation = (32.dp - (index * 4).dp).coerceAtLeast(12.dp),
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = Color(0xFF8EC5FF).copy(alpha = 0.5f),
                        spotColor = Color(0xFF8EC5FF).copy(alpha = 0.7f)
                    )
                    .clickable {
                        // TAP TO SELECT - removed drag functionality
                        onCardSelected(account)
                    },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                // Use the actual WalletCard design with recommendation type
                WalletCard(
                    account = account,
                    onCardClick = { /* Handled by parent clickable */ },
                    modifier = Modifier.fillMaxSize(),
                    tiltAngle = 0f,
                    scale = 1f,
                    alpha = 1f,
                    recommendationType = recommendationType
                )
            }
        }
    }
}