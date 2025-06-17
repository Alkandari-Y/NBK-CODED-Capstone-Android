package com.coded.capstone.Calender.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun CalendarBar(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val today = LocalDate.now()
    val startOfWeek = today.with(DayOfWeek.SUNDAY)

    // Year and Month selectors
    var expanded by remember { mutableStateOf(false) }
    val currentYear = selectedDate.year
    val years = (currentYear - 3..currentYear + 3).toList()
    val months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )
    val currentMonth = selectedDate.monthValue - 1
    var selectedYear by remember { mutableStateOf(currentYear) }
    var selectedMonth by remember { mutableStateOf(currentMonth) }

    val monthListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Scroll to selected month on first composition or when selectedMonth changes
    LaunchedEffect(selectedMonth) {
        monthListState.animateScrollToItem(selectedMonth)
    }

    // Calculate month grid
    val yearMonth = YearMonth.of(selectedYear, selectedMonth + 1)
    val firstDayOfMonth = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // Sunday = 0
    val totalCells = ((firstDayOfWeek + daysInMonth + 6) / 7) * 7 // always 6 rows

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, end = 48.dp, start = 16.dp)
            .height(302.5.dp)
    ) {
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
                                selectedYear = year
                                expanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.padding(horizontal = 2.dp))
            // Month Selector
            LazyRow(state = monthListState) {
                items(months) { month ->
                    val isCurrentMonth = (month == "January" && selectedYear == today.year)
                    Text(
                        text = month,
                        color = if (month == months[selectedMonth]) Color.DarkGray else Color.Gray,
                        fontWeight = if (month == months[selectedMonth] || isCurrentMonth) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .clickable {
                                selectedMonth = months.indexOf(month)
                                coroutineScope.launch { monthListState.animateScrollToItem(selectedMonth) }
                            }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp))
        // Day of week headers
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEachIndexed { i, day ->
                val isToday = (DayOfWeek.of(if (i == 0) 7 else i) == today.dayOfWeek)
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp),
                    color = Color.Gray,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Month grid
        for (week in 0 until 6) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (day in 0..6) {
                    val cell = week * 7 + day
                    val dateNum = cell - firstDayOfWeek + 1
                    val isInMonth = dateNum in 1..daysInMonth
                    val date = if (isInMonth) YearMonth.of(selectedYear, selectedMonth + 1).atDay(dateNum) else null
                    val isSelected = date == selectedDate
                    val isToday = date == today
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isInMonth && date != null) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(CircleShape)
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
                                        shape = CircleShape
                                    )
                                    .clickable { onDateSelected(date) }
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = date.dayOfMonth.toString(),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) Color.White else Color.DarkGray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

