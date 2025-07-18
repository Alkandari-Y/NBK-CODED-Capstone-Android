package com.coded.capstone.screens.onboarding

import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.coded.capstone.navigation.NavRoutes
import com.coded.capstone.R
import com.coded.capstone.respositories.CategoryRepository
import com.coded.capstone.viewModels.HomeScreenViewModel
import com.coded.capstone.data.responses.category.CategoryDto
import com.coded.capstone.data.requests.recommendation.SetFavCategoryRequest
import com.coded.capstone.viewModels.FavCategoryUiState
import kotlinx.coroutines.delay
import androidx.compose.foundation.gestures.detectTapGestures

fun getCategoryOnboardingIcon(categoryName: String): ImageVector {
    return when (categoryName.lowercase()) {
        "retail" -> Icons.Filled.Store
        "travel" -> Icons.Filled.Flight
        "dining" -> Icons.Filled.Restaurant
        "fashion" -> Icons.Filled.Checkroom
        "technology" -> Icons.Filled.Computer
        "hospitality" -> Icons.Filled.Hotel
        "education" -> Icons.Filled.School
        "entertainment" -> Icons.Filled.Theaters
        "personal care" -> Icons.Filled.Spa
        "wholesale" -> Icons.Filled.LocalGroceryStore
        else -> Icons.Filled.Category
    }
}

fun getCategoryOnboardingColor(categoryName: String): Color {
    return when (categoryName.lowercase()) {
        "retail" -> Color(0xFFF44336)
        "travel" -> Color(0xFF0EA5E9)
        "dining" -> Color(0xFFFFC107)
        "fashion" -> Color(0xFFEC4899)
        "technology" -> Color(0xFF8B5CF6)
        "hospitality" -> Color(0xFF084D35)
        "education" -> Color(0xFF6366F1)
        "entertainment" -> Color(0xFFE11D48)
        "personal care" -> Color(0xFF009688)
        "wholesale" -> Color(0xFFD97706)
        else -> Color(0xFF6B7280)
    }
}

@Composable
fun CategoryOnBoarding(navController: NavController, viewModel: HomeScreenViewModel) {

    var selectedCategories by remember { mutableStateOf(listOf<String>()) }
    val categories by viewModel.categories.collectAsState()
    val favCategoryUiState by viewModel.favCategoryUiState.collectAsState()

    val targetCategories = listOf(
        "retail", "travel", "dining", "fashion", "technology",
        "hospitality", "education", "entertainment", "personal care", "wholesale"
    )

    val filteredCategories = remember(categories) {
        val availableCategories = categories.filter { category ->
            targetCategories.contains(category.name.lowercase())
        }
        targetCategories.mapNotNull { targetName ->
            availableCategories.find { it.name.lowercase() == targetName }
        }.take(10)
    }

    LaunchedEffect(categories) {
        Log.d("CategoryOnBoarding", "Categories loaded: ${categories.size}")
        categories.forEach { category ->
            Log.d("CategoryOnBoarding", "Category: ${category.name} - ID: ${category.id}")
        }
    }

    LaunchedEffect(filteredCategories) {
        Log.d("CategoryOnBoarding", "Filtered categories: ${filteredCategories.size}")
        filteredCategories.forEach { category ->
            Log.d("CategoryOnBoarding", "Filtered: ${category.name}")
        }
    }

    var cardVisible by remember { mutableStateOf(false) }
    val cardOffsetY by animateDpAsState(
        targetValue = if (cardVisible) 80.dp else 800.dp,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "card_slide_up"
    )

    val cardAlpha by animateFloatAsState(
        targetValue = if (cardVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 600,
            delayMillis = 200,
            easing = FastOutSlowInEasing
        ),
        label = "card_fade_in"
    )

    var buttonPressed by remember { mutableStateOf(false) }
    val buttonScale by animateFloatAsState(
        targetValue = if (buttonPressed) 0.95f else 1f,
        animationSpec = tween(100),
        label = "button_scale"
    )

    val scrollState = rememberLazyGridState()

    LaunchedEffect(Unit) {
        delay(300)
        cardVisible = true
        viewModel.fetchCategories()
    }

    LaunchedEffect(favCategoryUiState) {
        when (favCategoryUiState) {
            is FavCategoryUiState.Success -> {
                navController.navigate(NavRoutes.NAV_ROUTE_VENDORS_ONBOARDING)
            }
            else -> {}
        }
    }

    fun toggleCategory(categoryId: String) {
        selectedCategories = if (selectedCategories.contains(categoryId)) {
            selectedCategories.filter { it != categoryId }
        } else if (selectedCategories.size < 3) {
            selectedCategories + categoryId
        } else {
            selectedCategories
        }
    }

    fun submitFavoriteCategories() {
        if (selectedCategories.size != 3) return
        viewModel.submitFavoriteCategories(selectedCategories)
    }

    Scaffold(
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = { submitFavoriteCategories() },
                        enabled = selectedCategories.size == 3 && favCategoryUiState !is FavCategoryUiState.Loading,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .height(44.dp)
                            .widthIn(min = 100.dp)
                            .scale(buttonScale)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        buttonPressed = true
                                        tryAwaitRelease()
                                        buttonPressed = false
                                    }
                                )
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8EC5FF),
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFFE5E7EB)
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (favCategoryUiState is FavCategoryUiState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "NEXT",
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                                Icon(
                                    imageVector = Icons.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF23272E))
                .padding(paddingValues)
        ) {
            Image(
                painter = painterResource(id = R.drawable.klue),
                contentDescription = "KLUE Logo",
                modifier = Modifier
                    .size(90.dp)
                    .offset(y = 50.dp)
                    .align(Alignment.TopCenter)
            )
            Spacer(modifier = Modifier.width(15.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxSize()
                    .offset(y = cardOffsetY)
                    .alpha(cardAlpha),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                shape = RoundedCornerShape(
                    topStart = 50.dp,
                    topEnd = 0.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                ),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 40.dp)
                        ) {
                            Text(
                                text = "Personalize Your Experience",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF374151),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                repeat(3) { index ->
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(
                                                if (index == 0) Color(0xFF374151) else Color(0xFFD1D5DB),
                                                CircleShape
                                            )
                                    )
                                    if (index < 2) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "Select up to 3 of your favorite categories",
                                fontSize = 14.sp,
                                color = Color(0xFF6B7280),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 4.dp)
                            )

                            Text(
                                text = "${selectedCategories.size}/3 selected",
                                fontSize = 14.sp,
                                color = Color(0xFF374151),
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                            )
                        }

                        Box(modifier = Modifier.weight(1f)) {
                            LazyVerticalGrid(
                                state = scrollState,
                                columns = GridCells.Fixed(2),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                contentPadding = PaddingValues(
                                    bottom = 100.dp
                                )
                            ) {
                                items(
                                    items = filteredCategories,
                                    key = { category -> category.id }
                                ) { category ->
                                    Card(
                                        modifier = Modifier
                                            .aspectRatio(1f)
                                            .fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        border = if (selectedCategories.contains(category.id.toString()))
                                            BorderStroke(2.dp, Color(0xFF8EC5FF)) else null,
                                        elevation = CardDefaults.cardElevation(
                                            defaultElevation = 2.dp
                                        ),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color.White
                                        ),
                                        onClick = {
                                            if (selectedCategories.contains(category.id.toString()) || selectedCategories.size < 3) {
                                                toggleCategory(category.id.toString())
                                            }
                                        }
                                    ) {
                                        Box(modifier = Modifier.fillMaxSize()) {
                                            if (selectedCategories.contains(category.id.toString())) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(20.dp)
                                                        .offset(x = (-6).dp, y = 6.dp)
                                                        .background(Color(0xFF8EC5FF), CircleShape)
                                                        .align(Alignment.TopEnd)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Check,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier
                                                            .size(12.dp)
                                                            .align(Alignment.Center)
                                                    )
                                                }
                                            }

                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(16.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Icon(
                                                    imageVector = getCategoryOnboardingIcon(category.name),
                                                    contentDescription = null,
                                                    tint = getCategoryOnboardingColor(category.name),
                                                    modifier = Modifier.size(36.dp)
                                                )

                                                Spacer(modifier = Modifier.height(12.dp))

                                                Text(
                                                    text = category.name,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (selectedCategories.contains(category.id.toString()))
                                                        Color(0xFF8EC5FF) else Color(0xFF23272E),
                                                    textAlign = TextAlign.Center,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            val scrollProgress = remember {
                                derivedStateOf {
                                    if (scrollState.layoutInfo.totalItemsCount == 0) 0f
                                    else {
                                        val firstVisibleItem = scrollState.firstVisibleItemIndex.toFloat()
                                        val totalItems = scrollState.layoutInfo.totalItemsCount.toFloat()
                                        (firstVisibleItem / (totalItems - 4)).coerceIn(0f, 1f)
                                    }
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 4.dp)
                                    .width(6.dp)
                                    .height(200.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Color(0xFF9CA3AF).copy(alpha = 0.3f),
                                            RoundedCornerShape(3.dp)
                                        )
                                )

                                Box(
                                    modifier = Modifier
                                        .width(6.dp)
                                        .height(40.dp)
                                        .offset(y = (160.dp * scrollProgress.value))
                                        .background(
                                            Color(0xFF9CA3AF).copy(alpha = 0.8f),
                                            RoundedCornerShape(3.dp)
                                        )
                                )
                            }
                        }
                    }

                    if (favCategoryUiState is FavCategoryUiState.Error) {
                        Card(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(16.dp)
                                .offset(y = (-80).dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Red.copy(alpha = 0.1f)
                            )
                        ) {
                            Text(
                                text = (favCategoryUiState as FavCategoryUiState.Error).message,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}