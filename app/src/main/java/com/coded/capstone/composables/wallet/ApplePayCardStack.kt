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
import androidx.compose.package com.coded.capstone.composables.wallet

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundpackage com.coded.capstone.composables.wallet

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

    // Drag state
    var draggedCardIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

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

            // Drag offset for this specific card
            val cardDragOffset = if (draggedCardIndex == index) dragOffset else 0f

            // Get recommendation type for this account
            val recommendationType = getRecommendationType(account)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .offset(y = baseOffset + cardDragOffset.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = if (draggedCardIndex == index && isDragging) {
                            (1f - (dragOffset / 500f)).coerceAtLeast(0.1f)
                        } else {
                            alpha
                        }
                    }
                    .zIndex(if (draggedCardIndex == index) 2000f else 1000f - index)
                    .shadow(
                        elevation = (32.dp - (index * 4).dp).coerceAtLeast(12.dp),
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = Color(0xFF8EC5FF).copy(alpha = 0.5f),
                        spotColor = Color(0xFF8EC5FF).copy(alpha = 0.7f)
                    )
                    .pointerInput(index) {
                        detectDragGestures(
                            onDragStart = {
                                draggedCardIndex = index
                                isDragging = true
                            },
                            onDragEnd = {
                                if (dragOffset > 200f) {
                                    // Card dragged far enough, trigger details page
                                    onCardSelected(account)
                                }
                                // Reset drag state
                                draggedCardIndex = null
                                dragOffset = 0f
                                isDragging = false
                            },
                            onDragCancel = {
                                draggedCardIndex = null
                                dragOffset = 0f
                                isDragging = false
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                // Slow down the drag by reducing sensitivity
                                dragOffset += dragAmount.y * 0.5f
                                // Limit drag to prevent going too far
                                dragOffset = dragOffset.coerceAtLeast(0f)
                            }
                        )
                    }
                    .clickable {
                        // Only select card if stack is already expanded
                        if (isExpanded) {
                            onCardSelected(account)
                        }
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

ation.layout.*
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

    // Drag state
    var draggedCardIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

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

            // Drag offset for this specific card
            val cardDragOffset = if (draggedCardIndex == index) dragOffset else 0f

            // Get recommendation type for this account
            val recommendationType = getRecommendationType(account)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .offset(y = baseOffset + cardDragOffset.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = if (draggedCardIndex == index && isDragging) {
                            (1f - (dragOffset / 500f)).coerceAtLeast(0.1f)
                        } else {
                            alpha
                        }
                    }
                    .zIndex(if (draggedCardIndex == index) 2000f else 1000f - index)
                    .shadow(
                        elevation = (32.dp - (index * 4).dp).coerceAtLeast(12.dp),
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = Color(0xFF8EC5FF).copy(alpha = 0.5f),
                        spotColor = Color(0xFF8EC5FF).copy(alpha = 0.7f)
                    )
                    .pointerInput(index) {
                        detectDragGestures(
                            onDragStart = {
                                draggedCardIndex = index
                                isDragging = true
                            },
                            onDragEnd = {
                                if (dragOffset > 200f) {
                                    // Card dragged far enough, trigger details page
                                    onCardSelected(account)
                                }
                                // Reset drag state
                                draggedCardIndex = null
                                dragOffset = 0f
                                isDragging = false
                            },
                            onDragCancel = {
                                draggedCardIndex = null
                                dragOffset = 0f
                                isDragging = false
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                // Slow down the drag by reducing sensitivity
                                dragOffset += dragAmount.y * 0.5f
                                // Limit drag to prevent going too far
                                dragOffset = dragOffset.coerceAtLeast(0f)
                            }
                        )
                    }
                    .clickable {
                        // Only select card if stack is already expanded
                        if (isExpanded) {
                            onCardSelected(account)
                        }
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

material3.CardDefaults
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

    // Drag state
    var draggedCardIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

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

            // Drag offset for this specific card
            val cardDragOffset = if (draggedCardIndex == index) dragOffset else 0f

            // Get recommendation type for this account
            val recommendationType = getRecommendationType(account)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .offset(y = baseOffset + cardDragOffset.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = if (draggedCardIndex == index && isDragging) {
                            (1f - (dragOffset / 500f)).coerceAtLeast(0.1f)
                        } else {
                            alpha
                        }
                    }
                    .zIndex(if (draggedCardIndex == index) 2000f else 1000f - index)
                    .shadow(
                        elevation = (32.dp - (index * 4).dp).coerceAtLeast(12.dp),
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = Color(0xFF8EC5FF).copy(alpha = 0.5f),
                        spotColor = Color(0xFF8EC5FF).copy(alpha = 0.7f)
                    )
                    .pointerInput(index) {
                        detectDragGestures(
                            onDragStart = {
                                draggedCardIndex = index
                                isDragging = true
                            },
                            onDragEnd = {
                                if (dragOffset > 200f) {
                                    // Card dragged far enough, trigger details page
                                    onCardSelected(account)
                                }
                                // Reset drag state
                                draggedCardIndex = null
                                dragOffset = 0f
                                isDragging = false
                            },
                            onDragCancel = {
                                draggedCardIndex = null
                                dragOffset = 0f
                                isDragging = false
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                // Slow down the drag by reducing sensitivity
                                dragOffset += dragAmount.y * 0.5f
                                // Limit drag to prevent going too far
                                dragOffset = dragOffset.coerceAtLeast(0f)
                            }
                        )
                    }
                    .clickable {
                        // Only select card if stack is already expanded
                        if (isExpanded) {
                            onCardSelected(account)
                        }
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

