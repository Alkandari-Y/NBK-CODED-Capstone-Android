package com.coded.capstone.screens.calender

import android.util.Log
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.coded.capstone.composables.calendar.CalendarBar
import com.coded.capstone.composables.calendar.CalendarBarShort
import com.coded.capstone.composables.calendar.common.RoundedRightPeek
import com.coded.capstone.MapAndGeofencing.MapScreen
import com.coded.capstone.viewModels.RecommendationViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.coded.capstone.data.responses.category.CategoryDto
import com.coded.capstone.data.responses.promotion.PromotionResponse
import com.coded.capstone.respositories.CategoryRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(viewModel: RecommendationViewModel = RecommendationViewModel(LocalContext.current)) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var isExpanded by remember { mutableStateOf(false) }
    var selectedYear by remember { mutableStateOf(selectedDate.year) }
    var selectedMonth by remember { mutableStateOf(selectedDate.monthValue - 1) }
    var showMap by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Long?>(null) }
    var expandedPromotionId by remember { mutableStateOf<Long?>(null) }
    val context = LocalContext.current

    // Collect promotions and categories
    val promotions by viewModel.promotions.collectAsState()
    val categories = CategoryRepository.categories

    // Fetch promotions and categories when screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.fetchPromotions()
    }

    // Filter promotions for selected date and category
    val filteredPromotions = remember(selectedDate, selectedCategory, promotions) {
        promotions.filter { promotion ->
            val dateMatches = (selectedDate.isEqual(promotion.startDate) || selectedDate.isEqual(promotion.endDate) ||
                    (selectedDate.isAfter(promotion.startDate) && selectedDate.isBefore(promotion.endDate)))
            
            val categoryMatches = selectedCategory == null || 
                    categories.find { it.id == selectedCategory }?.let { selectedCat ->
                        viewModel.partners.value.find { it.id == promotion.businessPartnerId }?.category?.id == selectedCat.id
                    } ?: false

            dateMatches && categoryMatches
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Draw vertical dark gray block on the right covering the bottom half
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .width(60.dp)
                .height(780.dp)
                .background(Color(0xFF23272E))
        )
        // Draw RoundedRightPeek so it overlays the dark gray block
        Box(modifier = Modifier.align(Alignment.TopEnd).offset(y = 64.dp)) {
            RoundedRightPeek()
        }

        Column(Modifier.fillMaxSize()) {
            val calendarTopPadding = 0.dp
            val cardPadding = 32.dp

            // Animate the calendar height
            val calendarHeight by animateDpAsState(
                targetValue = if (isExpanded) 400.dp else 200.dp,
                animationSpec = tween(durationMillis = 500, easing = EaseInOutCubic),
                label = "calendarHeight"
            )

            Card(
                modifier = Modifier
                    .padding(top = calendarTopPadding, start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
                    .height(calendarHeight),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column {
                    if (isExpanded) {
                        CalendarBar(
                            selectedDate = selectedDate,
                            onDateSelected = {
                                selectedDate = it
                            }
                        )
                    } else {
                        CalendarBarShort(
                            selectedDate = selectedDate,
                            onDateSelected = {
                                selectedDate = it
                            },
                            selectedYear = selectedYear,
                            selectedMonth = selectedMonth,
                            onYearMonthChange = { year, month ->
                                selectedYear = year
                                selectedMonth = month
                                selectedDate = LocalDate.of(year, month + 1, 1)
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
            // Dark gray background fills the rest
            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 70.dp, topEnd = 60.dp))
                    .background(Color(0xFF23272E))
            ) {
                val dragThreshold = 20f
                var dragOffset by remember { mutableStateOf(0f) }
                var isDragging by remember { mutableStateOf(false) }
                val draggableState = rememberDraggableState { delta ->
                    dragOffset += delta
                    isDragging = true
                    if (!isExpanded && dragOffset > dragThreshold) {
                        isExpanded = true
                        dragOffset = 0f
                        isDragging = false
                    } else if (isExpanded && dragOffset < -dragThreshold) {
                        isExpanded = false
                        dragOffset = 0f
                        isDragging = false
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .padding(top = 5.dp, bottom = 5.dp)
                ) {
                    // Draggable handle
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth(0.3f)
                            .height(32.dp)
                            .draggable(
                                state = draggableState,
                                orientation = Orientation.Vertical,
                                onDragStarted = { isDragging = true },
                                onDragStopped = { dragOffset = 0f; isDragging = false }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (isDragging) Color.DarkGray else Color.Gray)
                        )
                    }
                    Spacer(modifier = Modifier.height(0.dp))

                    // Page title
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Offers Calendar",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        
                        IconButton(
                            onClick = { showMap = true },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = "Open Map",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Date text
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "Offers for ",
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = selectedDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Category Filter Dropdown
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        var expanded by remember { mutableStateOf(false) }

                        Box {
                            Button(
                                onClick = { expanded = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF8EC5FF),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    text = categories.find { it.id == selectedCategory }?.name ?: "All Categories",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Show categories"
                                )
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier
                                    .background(Color(0xFF2A2A2A), RoundedCornerShape(12.dp))
                                    .width(200.dp)
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "All Categories",
                                            color = Color.White
                                        )
                                    },
                                    onClick = {
                                        selectedCategory = null
                                        expanded = false
                                    },
                                    modifier = Modifier.background(
                                        if (selectedCategory == null) Color(0xFF666666) else Color.Transparent
                                    ),
                                    colors = MenuDefaults.itemColors(
                                        textColor = Color.White
                                    )
                                )

                                categories.forEach { category ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                category.name,
                                                color = Color.White
                                            )
                                        },
                                        onClick = {
                                            selectedCategory = category.id
                                            expanded = false
                                        },
                                        modifier = Modifier.background(
                                            if (selectedCategory == category.id) Color(0xFF666666) else Color.Transparent
                                        ),
                                        colors = MenuDefaults.itemColors(
                                            textColor = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        if (filteredPromotions.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No offers available for this date",
                                    color = Color.White.copy(alpha = 0.7f),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                items(filteredPromotions) { promotion ->
                                    PromotionItem(
                                        promotion = promotion,
                                        isExpanded = expandedPromotionId == promotion.id,
                                        onToggleExpansion = {
                                            expandedPromotionId = if (expandedPromotionId == promotion.id) null else promotion.id
                                        }
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(5.dp))
                }
            }
        }

        if (showMap) {
            MapScreen(onClose = { showMap = false })
        }
    }
}

@Composable
fun PromotionItem(
    promotion: PromotionResponse,
    isExpanded: Boolean,
    onToggleExpansion: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggleExpansion),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF374151)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = promotion.name,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = promotion.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f)
            )

            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                
                // Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFF8EC5FF).copy(alpha = 0.3f))
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Start Date",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF8EC5FF)
                        )
                        Text(
                            text = promotion.startDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "End Date",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF8EC5FF)
                        )
                        Text(
                            text = promotion.endDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Type: ${promotion.type}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF8EC5FF)
                    )
                    
                    // Expand/Collapse indicator
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = Color(0xFF8EC5FF),
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Type: ${promotion.type}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF8EC5FF)
                    )
                    
                    // Expand/Collapse indicator
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = Color(0xFF8EC5FF),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}



