package com.coded.capstone.composables.wallet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.coded.capstone.data.responses.account.AccountResponse

@Composable
 fun ApplePayCardStack(
    accounts: List<AccountResponse>,
    selectedCard: AccountResponse?,
    pagerState: PagerState,
    scrollVelocity: Float,
    onCardSelected: (AccountResponse) -> Unit,
    onScrollVelocityChange: (Float) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        accounts.forEachIndexed { index, account ->
            val baseOffset = (index * 48).dp

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .offset(y = baseOffset)
                    .graphicsLayer {
                        scaleX = 0.98f - (index * 0.02f)
                        scaleY = 0.98f - (index * 0.02f)
                        alpha = (1f - (index * 0.06f)).coerceAtLeast(0.4f)
                    }
                    .zIndex(1000f - index)
                    .shadow(
                        elevation = (20.dp - (index * 2).dp).coerceAtLeast(4.dp),
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = Color.Black.copy(alpha = 0.3f),
                        spotColor = Color.Black.copy(alpha = 0.5f)
                    )
                    .clickable(
                        indication = ripple(color = Color.White.copy(alpha = 0.2f)),
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        onCardSelected(account)
                    },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                WalletCard(
                    account = account,
                    onCardClick = { onCardSelected(account) },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
