package com.coded.capstone.Wallet.components

import androidx.compose.animation.core.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.animation.core.FastOutSlowInEasing
import kotlinx.coroutines.delay

@Composable
fun CardStack(
    accounts: List<AccountResponse>,
    selectedCard: AccountResponse?,
    onCardSelected: (AccountResponse?) -> Unit,
    modifier: Modifier = Modifier,
    showAccountNumber: Boolean = false
) {
    var cardOrder by remember { mutableStateOf(accounts) }
    var swipeDirection by remember { mutableStateOf(0f) }
    var totalDrag by remember { mutableStateOf(0f) }
    var animatingOutCard by remember { mutableStateOf<AccountResponse?>(null) }
    val scope = rememberCoroutineScope()

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        cardOrder.forEachIndexed { index, card ->
            val isTopCard = index == 0
            val isAnimatingOut = card == animatingOutCard
            
            // Skip rendering if this card is animating out
            if (!isAnimatingOut) {
                val offsetY by animateFloatAsState(
                    targetValue = when {
                        isTopCard && swipeDirection == -1f -> -screenHeightPx // exit off the top
                        isTopCard && swipeDirection == 1f -> 300f
                        else -> (index * 16).toFloat()
                    },
                    animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
                )
                val gradient = remember(card.id) {
                    when (accounts.indexOf(card) % 5) {
                        0 -> Brush.verticalGradient(listOf(Color(0xFF12222D), Color.Black))
                        1 -> Brush.verticalGradient(listOf(Color(0xFF1C2028), Color.DarkGray))
                        2 -> Brush.verticalGradient(listOf(Color(0xFF1D162A), Color(0xFF3F5B8C)))
                        3 -> Brush.verticalGradient(listOf(Color(0x92192026), Color(0xFF0F242C)))
                        else -> Brush.verticalGradient(listOf(Color.DarkGray, Color.Black))
                    }
                }
                Box(
                    modifier = Modifier
                        .offset { IntOffset(0, offsetY.roundToInt()) }
                        .size(360.dp, 220.dp)
                        .shadow(16.dp, RoundedCornerShape(16.dp))
                        .zIndex((100 - index).toFloat())
                        .then(
                            if (isTopCard) Modifier.pointerInput(cardOrder) {
                                detectVerticalDragGestures(
                                    onDragEnd = {
                                        if (totalDrag < -100f && cardOrder.size > 1) {
                                            swipeDirection = -1f
                                            animatingOutCard = cardOrder.first()
                                            // Update card order after animation completes
                                            scope.launch {
                                                delay(650)
                                                cardOrder = cardOrder.drop(1) + cardOrder.first()
                                                swipeDirection = 0f
                                                totalDrag = 0f
                                                animatingOutCard = null
                                            }
                                        } else if (totalDrag > 100f) {
                                            onCardSelected(cardOrder.first())
                                            swipeDirection = 0f
                                            totalDrag = 0f
                                        } else {
                                            swipeDirection = 0f
                                            totalDrag = 0f
                                        }
                                    },
                                    onVerticalDrag = { change: PointerInputChange, dragAmount: Float ->
                                        totalDrag += dragAmount
                                        swipeDirection = when {
                                            totalDrag < -10f -> -1f
                                            totalDrag > 10f -> 1f
                                            else -> 0f
                                        }
                                    }
                                )
                            } else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    PaymentCardView(
                        card = card,
                        backgroundGradient = gradient,
                        showAccountNumber = showAccountNumber
                    )
                }
            }
        }
    }
} 