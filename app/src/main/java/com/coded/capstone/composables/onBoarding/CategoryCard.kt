package com.coded.capstone.composables.onBoarding

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coded.capstone.data.responses.category.CategoryDto
//import com.coded.capstone.screens.onboarding.SpendingCategory
import androidx.compose.foundation.clickable

@Composable
fun CategoryCard(
    category: CategoryDto,
    isSelected: Boolean,
    isDisabled: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .scale(scale)
            .then(if (!isDisabled) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            2.dp,
            if (isSelected) Color(0xFF8EC5FF) else Color.White.copy(alpha = 0.3f)
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                Color(0xFF8EC5FF).copy(alpha = 0.15f)
            } else if (isDisabled) {
                Color.White.copy(alpha = 0.1f)
            } else {
                Color.White.copy(alpha = 0.15f)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 6.dp else 2.dp
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Text(
                text = category.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDisabled) Color.White.copy(alpha = 0.4f) else Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
    }
}