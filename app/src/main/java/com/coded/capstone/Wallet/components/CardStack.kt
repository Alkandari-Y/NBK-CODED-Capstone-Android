package com.coded.capstone.Wallet.components

import androidx.compose.animation.core.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.coded.capstone.Wallet.data.CardState
import com.coded.capstone.data.responses.account.AccountResponse

@Composable
fun CardStack(
    accounts: List<AccountResponse>,
    selectedCard: AccountResponse?,
    onCardSelected: (AccountResponse?) -> Unit,
    modifier: Modifier = Modifier
) {
    val cardStates = remember(accounts) {
        accounts.map { CardState(it) }
    }
    
    val visibleCards = if (selectedCard != null) {
        cardStates.filter { it.card == selectedCard }
    } else {
        cardStates.take(3) // Show only top 3 cards when not focused
    }
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        visibleCards.forEachIndexed { index, cardState ->
            val isSelected = cardState.card == selectedCard
            val isTopCard = index == 0
            
            val scale by animateFloatAsState(
                targetValue = when {
                    isSelected -> 1.0f
                    isTopCard -> 0.95f
                    else -> 0.9f
                },
                animationSpec = spring()
            )
            
            val offsetY by animateFloatAsState(
                targetValue = when {
                    isSelected -> 0f
                    isTopCard -> 0f
                    else -> (index * 20).toFloat()
                },
                animationSpec = spring()
            )
            
            val animatedAlpha by animateFloatAsState(
                targetValue = when {
                    isSelected -> 1f
                    isTopCard -> 0.9f
                    else -> 0.7f
                },
                animationSpec = spring()
            )
            
            val gradient = remember(accounts.indexOf(cardState.card)) {
                when (accounts.indexOf(cardState.card) % 5) {
                    0 -> Brush.verticalGradient(listOf(Color(0xFF8E77BB), Color.Black))
                    1 -> Brush.verticalGradient(listOf(Color(0xFF231E31), Color.DarkGray))
                    2 -> Brush.verticalGradient(listOf(Color(0xFF1D162A), Color(0xFF9688B9)))
                    3 -> Brush.verticalGradient(listOf(Color(0xFF1C1926), Color(0xFF191623)))
                    else -> Brush.verticalGradient(listOf(Color.DarkGray, Color.Black))
                }
            }
            
            Box(
                modifier = Modifier
                    .size(width = 320.dp, height = 200.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationY = offsetY
                        alpha = animatedAlpha
                    }
                    .shadow(
                        elevation = if (isSelected) 16.dp else 8.dp,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .zIndex(if (isSelected) 999f else (100 - index).toFloat())
                    .pointerInput(cardState.card.id) {
                        detectTapGestures(
                            onTap = {
                                if (isSelected) {
                                    onCardSelected(null) // Deselect
                                } else {
                                    onCardSelected(cardState.card) // Select
                                }
                            }
                        )
                    }
            ) {
                PaymentCardView(
                    card = cardState.card,
                    backgroundGradient = gradient
                )
            }
        }
    }
} 