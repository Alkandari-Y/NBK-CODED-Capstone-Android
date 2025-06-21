package com.coded.capstone.wallet.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.coded.capstone.wallet.data.WalletAccountDisplayModel
import kotlin.math.abs

@Composable
fun CardStack(
    cards: List<WalletAccountDisplayModel>,
    selectedCardId: Long?,
    onCardSelected: (Long?) -> Unit,
    newCardAnimation: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Track previous card count for new card detection
    var previousCardCount by remember { mutableIntStateOf(cards.size) }
    val hasNewCard = cards.size > previousCardCount && newCardAnimation

    // scroll velocity tracking for tilt effect
    var scrollVelocity by remember { mutableFloatStateOf(0f) }
    var scrollOffset by remember { mutableFloatStateOf(0f) }

    // Global tilt animation based on scroll velocity
    val globalTiltAngle = remember { Animatable(0f) }

    // single card animations
    val cardAnimations = remember(cards.size) {
        cards.map { Animatable(0f) }
    }

    // Update previous count
    LaunchedEffect(cards.size) {
        if (cards.size > previousCardCount) {
            // New card detected - trigger special animation
        }
        previousCardCount = cards.size
    }

    // Dynamic tilt animation based on scroll velocity
    LaunchedEffect(scrollVelocity) {
        val targetTilt = when {
            abs(scrollVelocity) < 50f -> 0f // No tilt for small movements
            else -> (scrollVelocity * 0.003f).coerceIn(-8f, 8f) // Subtle tilt range
        }

        globalTiltAngle.animateTo(
            targetValue = targetTilt,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    // NestedScrollConnection for precise velocity
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: androidx.compose.ui.geometry.Offset,
                source: NestedScrollSource
            ): androidx.compose.ui.geometry.Offset {
                val delta = available.y
                scrollVelocity = delta * 2f
                scrollOffset += delta
                return androidx.compose.ui.geometry.Offset.Zero
            }

            override suspend fun onPreFling(
                available: Velocity
            ): Velocity {
                scrollVelocity = available.y * 0.5f
                return Velocity.Zero
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .nestedScroll(nestedScrollConnection)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        //  reduce velocity when drag ends
                        scrollVelocity *= 0.8f
                    }
                ) { _, dragAmount ->
                    scrollVelocity = dragAmount.y * 3f
                }
            },
        contentAlignment = Alignment.TopCenter
    ) {
        cards.forEachIndexed { index, card ->
            val isSelected = card.id == selectedCardId
            val isNewestCard = hasNewCard && index == cards.size - 1

            // dynamic tilt based on card position and scroll
            val cardTiltMultiplier = 1f - (index * 0.2f) // Back cards tilt less
            val dynamicTilt by remember {
                derivedStateOf {
                    when {
                        isSelected -> globalTiltAngle.value * 0.3f // Reduced tilt when selected
                        selectedCardId != null -> 0f // No tilt when other card selected
                        else -> {
                            val baseTilt = (index - 1) * 1.5f // og static tilt
                            val scrollTilt = globalTiltAngle.value * cardTiltMultiplier
                            baseTilt + scrollTilt
                        }
                    }
                }
            }

            val animatedOffsetY by animateDpAsState(
                targetValue = when {
                    isSelected -> (-20).dp  // Selected card moves up
                    selectedCardId != null -> (-250).dp  // Others slide up off-screen
                    else -> (index * 16).dp  // Normal stacking with offset
                },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = if (isNewestCard) Spring.StiffnessMedium else Spring.StiffnessLow
                ),
                label = "cardOffsetY"
            )

            //Rotation with dynamic scroll-based tilting
            val animatedRotation by animateFloatAsState(
                targetValue = dynamicTilt,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "cardRotation"
            )

            //  animation for depth perception
            val animatedScale by animateFloatAsState(
                targetValue = when {
                    isSelected -> 1.05f  // Selected card slightly larger
                    selectedCardId != null -> 0.85f  // Others smaller when disappearing
                    isNewestCard -> 1.02f  // New card slightly emphasized
                    else -> 1f - (index * 0.02f)  // Gradual scale reduction in stack
                },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = if (isNewestCard) Spring.StiffnessMedium else Spring.StiffnessLow
                ),
                label = "cardScale"
            )

            //fade effects
            val animatedOpacity by animateFloatAsState(
                targetValue = when {
                    isSelected -> 1f
                    selectedCardId != null -> 0f  // Others fade out completely
                    isNewestCard -> 1f  // New card fully visible
                    else -> 1f - (index * 0.08f)  // Subtle fade in stack
                },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "cardOpacity"
            )

            // Depth effect based on scroll velocity
            val depthEffect by remember {
                derivedStateOf {
                    abs(scrollVelocity) * 0.0001f
                }
            }

            WalletCard(
                card = card,
                isSelected = isSelected,
                onClick = {
                    if (isSelected) {
                        onCardSelected(null)  // Deselect if selected
                    } else {
                        onCardSelected(card.id)  // Select this card
                    }
                },
                modifier = Modifier
                    .zIndex(if (isSelected) 20f else (cards.size - index).toFloat())
                    .graphicsLayer {
                        // Position transformations
                        translationY = animatedOffsetY.toPx()

                        //  rotation with scroll based tilting
                        rotationZ = animatedRotation

                        // Scale with depth effect
                        val dynamicScale = animatedScale + depthEffect
                        scaleX = dynamicScale
                        scaleY = dynamicScale

                        // Opacity
                        alpha = animatedOpacity

                        // 3D effect for  tilting
                        cameraDistance = 12f * density
                        transformOrigin = androidx.compose.ui.graphics.TransformOrigin.Center

                        //  shadow effect based on tilt angle
                        shadowElevation = abs(animatedRotation) * 0.8f
                    }
            )
        }
    }
}