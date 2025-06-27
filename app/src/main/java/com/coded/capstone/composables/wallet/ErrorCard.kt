package com.coded.capstone.composables.wallet

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.respositories.AccountProductRepository

@Composable
 fun SingleSelectedCard(
    account: AccountResponse,
    onCardClick: () -> Unit
) {
    // Function to get recommendation type from account product category names
    fun getRecommendationType(account: AccountResponse): String? {
        // Get the account product to match the recommendation screen logic
        val accountProduct = AccountProductRepository.accountProducts.find {
            it.id == account.accountProductId
        }
        
        // Use the same logic as the recommendation screen
        return when {
            accountProduct?.name?.lowercase()?.contains("travel") == true -> "travel"
            accountProduct?.name?.lowercase()?.contains("family") == true -> "family essentials"
            accountProduct?.name?.lowercase()?.contains("entertainment") == true -> "entertainment"
            accountProduct?.name?.lowercase()?.contains("shopping") == true -> "shopping"
            accountProduct?.name?.lowercase()?.contains("dining") == true -> "dining"
            accountProduct?.name?.lowercase()?.contains("health") == true -> "health"
            accountProduct?.name?.lowercase()?.contains("education") == true -> "education"
            account.accountType?.lowercase() == "credit" -> "shopping"
            account.accountType?.lowercase() == "savings" -> "family essentials"
            account.accountType?.lowercase() == "debit" -> "travel"
            else -> null // Use default account type colors instead of defaulting to shopping
        }
    }

    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "selectedCardScale"
    )

    val offset by animateDpAsState(
        targetValue = 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "selectedCardOffset"
    )

    // Get recommendation type for this account
    val recommendationType = getRecommendationType(account)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .offset(y = offset)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = 24.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.3f),
                spotColor = Color.Black.copy(alpha = 0.5f)
            )
            .clickable(
                indication = ripple(color = Color.White.copy(alpha = 0.2f)),
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onCardClick()
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        WalletCard(
            account = account,
            onCardClick = onCardClick,
            modifier = Modifier.fillMaxSize(),
            recommendationType = recommendationType
        )
    }
}

@Composable
 fun ErrorCard(onRetry: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1F23)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Error loading accounts",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = "Please try again",
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 8.dp)
            )
            Button(
                onClick = onRetry,
                modifier = Modifier.padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))
            ) {
                Text("Retry", color = Color.White)
            }
        }
    }
}

@Composable
 fun EmptyAccountsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1F23)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.AccountBalanceWallet,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.3f),
                modifier = Modifier.size(80.dp)
            )
            Text(
                text = "No accounts found",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(top = 20.dp)
            )
            Text(
                text = "Add your first account to get started",
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}