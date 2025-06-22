package com.coded.capstone.composables.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
fun CalendarBarShort(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    selectedYear: Int,
    selectedMonth: Int,
    onYearMonthChange: (Int, Int) -> Unit
) {
    val today = LocalDate.now()
    // Generate the first day of the selected month
    val firstDayOfMonth = LocalDate.of(selectedYear, selectedMonth + 1, 1)
    // Find the start of the week containing the first day of the month
    val startOfWeek = firstDayOfMonth.with(DayOfWeek.SUNDAY)
    // Show 6 weeks for the month view
    val weekDates = (0 until 6 * 7).map { startOfWeek.plusDays(it.toLong()) }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = weekDates.indexOf(selectedDate).coerceAtLeast(0))

    // Scroll to selected date when it changes
    LaunchedEffect(selectedDate) {
        val index = weekDates.indexOf(selectedDate)
        if (index != -1) {
            listState.animateScrollToItem(index)
        }
    }

    // Year and Month selectors
    var expanded by remember { mutableStateOf(false) }
    val years = (selectedYear - 3..selectedYear + 3).toList()
    val months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )
    val currentMonth = today.monthValue - 1
    val monthsListState = rememberLazyListState()

    // Scroll to selected month when it changes
    LaunchedEffect(selectedMonth) {
        monthsListState.animateScrollToItem(selectedMonth)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, end = 48.dp, start = 16.dp)
            .height(150.dp) // Updated height to match MainScreen
    ) {
        // Year and Month selectors
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Year Dropdown
            Box {
                Text(
                    text = selectedYear.toString(),
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { expanded = true }
                )
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.DarkGray)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    years.forEach { year ->
                        DropdownMenuItem(
                            text = { Text(year.toString()) },
                            onClick = {
                                onYearMonthChange(year, selectedMonth)
                                expanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            // Month Selector
            LazyRow(state = monthsListState) {
                items(months) { month ->
                    val index = months.indexOf(month)
                    val isCurrentMonth = (index == currentMonth && selectedYear == today.year)
                    val isSelectedMonth = (index == selectedMonth)
                    Text(
                        text = month,
                        color = if (isSelectedMonth) Color.DarkGray else Color.Gray,
                        fontWeight = if (isSelectedMonth || isCurrentMonth) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .clickable { onYearMonthChange(selectedYear, index) }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Single LazyRow for day titles and dates, scrollable through the month
        LazyRow(state = listState) {
            items(weekDates) { date ->
                val isSelected = date == selectedDate
                val isToday = date == today
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            when {
                                isSelected -> Color.Black
                                isToday -> Color(0xFFE0E0E0)
                                else -> Color.Transparent
                            }
                        )
                        .border(
                            width = if (isToday && !isSelected) 2.dp else 0.dp,
                            color = if (isToday && !isSelected) Color.Black else Color.Transparent,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .clickable { onDateSelected(date) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = date.dayOfWeek.name.take(1),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp),
                            color = if (isSelected) Color.White else Color.Gray,
                            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = date.dayOfMonth.toString(),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isSelected) Color.White else Color.DarkGray
                        )
                    }
                }
            }
        }
    }
} 