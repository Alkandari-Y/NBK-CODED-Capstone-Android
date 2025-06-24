package com.coded.capstone.composables.onBoarding

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coded.capstone.data.requests.partner.PartnerDto
import com.coded.capstone.screens.onboarding.getCategoryIcon

@Composable
fun NBKVendorCard(
    vendor: PartnerDto,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "vendor_scale"
    )

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .scale(scale),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            2.dp,
            if (isSelected) Color(0xFF4CAF50) else Color(0xFFE5E7EB)
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                Color(0xFF4CAF50).copy(alpha = 0.08f)
            } else {
                Color.White
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Selection indicator
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            Color(0xFF4CAF50),
                            CircleShape
                        )
                        .align(Alignment.TopEnd)
                        .offset((-8).dp, 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Logo - use AsyncImage for network image
                if (vendor.logoUrl.isNotEmpty()) {
                    AsyncImage(
                        model = vendor.logoUrl,
                        contentDescription = "${vendor.name} logo",
                        modifier = Modifier
                            .size(35.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    // Fallback to category icon
                    Icon(
                        imageVector = getCategoryIcon(vendor.category.name),
                        contentDescription = "${vendor.category.name} category",
                        tint = Color(0xFF03A9F4),
                        modifier = Modifier
                            .size(35.dp)
                            .padding(bottom = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = vendor.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = vendor.category.name,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
    }
}