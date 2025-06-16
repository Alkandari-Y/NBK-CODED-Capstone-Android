package com.coded.capstone.Calender.calendar.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RoundedRightPeek() {
    Box(
        modifier = Modifier
            .width(16.dp)
            .height(550.dp)
            .clip(RoundedCornerShape(topStart = 100.dp, bottomStart = 100.dp))
            .background(Color.Black)
    )
} 