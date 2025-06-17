package com.coded.capstone.screens

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.coded.capstone.Calender.calendar.CalendarBar
import com.coded.capstone.Calender.calendar.CalendarBarShort
import com.coded.capstone.Calender.calendar.common.RoundedRightPeek
import com.coded.capstone.Calender.calendar.offers.FilterChip
import com.coded.capstone.Calender.calendar.offers.OfferDetailsDialog
import com.coded.capstone.Calender.calendar.offers.OfferItem
import com.coded.capstone.MapAndGeofencing.MapScreen
import com.coded.capstone.data.Tmp.Offer
import com.coded.capstone.data.Tmp.repository.OfferRepository
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var isExpanded by remember { mutableStateOf(false) }
    var selectedYear by remember { mutableStateOf(selectedDate.year) }
    var selectedMonth by remember { mutableStateOf(selectedDate.monthValue - 1) }
    var showMap by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var expandedOfferId by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    // Get offers for the selected date
    val offers = remember(selectedDate, selectedCategory) {
        val date = Date.from(selectedDate.atStartOfDay().toInstant(java.time.ZoneOffset.UTC))
        Log.d("MainScreen", "Selected date: ${selectedDate}, converted to Date: ${SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(date)}")
        OfferRepository.getOffersForDate(context, date, selectedCategory)
    }

    val categories = remember {
        OfferRepository.getAvailableCategories(OfferRepository.loadOffers(context))
    }

    // Log when offers change
    LaunchedEffect(offers) {
        Log.d("MainScreen", "Offers updated: ${offers.size} offers available")
    }

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
        // Draw RoundedRightPeek so it overlays the black block
        Box(modifier = Modifier.align(Alignment.TopEnd).offset(y = 64.dp)) {
            RoundedRightPeek()
        }

        Column(Modifier.fillMaxSize()) {
            val calendarTopPadding = 32.dp
            val cardPadding = 32.dp

            // Animate the calendar height
            val calendarHeight by animateDpAsState(
                targetValue = if (isExpanded) 302.5.dp else 150.dp,
                animationSpec = tween(durationMillis = 500, easing = androidx.compose.animation.core.EaseInOutCubic),
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
            // Black background fills the rest
            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 70.dp, topEnd = 60.dp))
                    .background(Color.Black)
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
                        .padding(top = 24.dp, bottom = 32.dp)
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
                    Spacer(modifier = Modifier.height(16.dp))

                    // Page title
                    Text(
                        text = "Offers Calendar",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

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
                            text = selectedDate.format(java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy")),
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
                                    containerColor = Color(0xFF2A2A2A),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    text = selectedCategory ?: "All Categories",
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
                                                category,
                                                color = Color.White
                                            )
                                        },
                                        onClick = {
                                            selectedCategory = category
                                            expanded = false
                                        },
                                        modifier = Modifier.background(
                                            if (selectedCategory == category) Color(0xFF666666) else Color.Transparent
                                        ),
                                        colors = MenuDefaults.itemColors(
                                            textColor = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Offers list with weight to take remaining space
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        if (offers.isEmpty()) {
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
                                items(offers) { offer ->
                                    val offerId = when (offer) {
                                        is Offer.SingleDate -> "${offer.name}_${offer.date}"
                                        is Offer.DateRange -> "${offer.name}_${offer.startDate}_${offer.endDate}"
                                    }

                                    OfferItem(
                                        offer = offer,
                                        onClick = { },
                                        isExpanded = expandedOfferId == offerId,
                                        onToggleExpansion = {
                                            expandedOfferId = if (expandedOfferId == offerId) null else offerId
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Map button
                    Button(
                        onClick = { showMap = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E88E5)
                        )
                    ) {
                        Text("Open Map")
                    }
                }
            }
        }

        if (showMap) {
            MapScreen(onClose = { showMap = false })
        }
    }
}

@Composable
fun CalendarScreen() {
    MainScreen()
}





