package com.coded.capstone.composables.businessPartners

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coded.capstone.R
import com.coded.capstone.data.responses.promotion.PromotionResponse
import java.time.LocalDate


//to get logo resource name from business name
@DrawableRes
fun getBusinessLogoResource(context: android.content.Context, businessName: String?): Int {
    if (businessName.isNullOrBlank()) return R.drawable.default_promotion

    val resourceName = businessName
        .lowercase()
        .replace(" ", "_")
        .replace("&", "and")
        .replace(".", "")
        .replace("-", "_")

    val resourceId = context.resources.getIdentifier(
        resourceName,
        "drawable",
        context.packageName
    )

    return if (resourceId != 0) resourceId else R.drawable.default_promotion
}

//expired promo logic
fun isPromotionExpired(promotion: PromotionResponse): Boolean {
    return LocalDate.now().isAfter(promotion.endDate)
}

fun getDaysUntilExpiration(promotion: PromotionResponse): Long {
    return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), promotion.endDate)
}

@Composable
fun BusinessLogo(
    businessName: String?,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 120.dp,
    isExpired: Boolean = false,
    showExpiredOverlay: Boolean = true,
    shape: Shape? = null, // Optional shape - null means no shape
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current
    val logoRes = if (isExpired) {
        R.drawable.expired_promotion
    } else {
        getBusinessLogoResource(context, businessName)
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        if (logoRes == R.drawable.default_promotion && !isExpired) {
            // Fallback - with optional shape
            Box(
                modifier = Modifier
                    .size(size)
                    .background(
                        Color.Gray.copy(alpha = 0.1f),
                        shape ?: RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                businessName?.let { name ->
                    Text(
                        text = name.take(2).uppercase(),
                        fontSize = (size.value * 0.25f).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                } ?: Icon(
                    imageVector = Icons.Default.Business,
                    contentDescription = "Business",
                    tint = Color.Gray,
                    modifier = Modifier.size(size * 0.4f)
                )
            }
        } else {
            // Image with optional shape
            val imageModifier = if (shape != null) {
                Modifier
                    .size(size)
                    .clip(shape)
            } else {
                Modifier.size(size)
            }

            Image(
                painter = painterResource(id = logoRes),
                contentDescription = "$businessName logo",
                modifier = imageModifier,
                contentScale = contentScale,
                colorFilter = if (isExpired) ColorFilter.tint(Color.Gray.copy(alpha = 0.6f)) else null
            )

            // expired overlay
            if (isExpired && showExpiredOverlay) {
                Box(
                    modifier = Modifier
                        .size(size)
                        .background(
                            Color.Black.copy(alpha = 0.5f),
                            shape ?: RoundedCornerShape(0.dp) // No shape for overlay too
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.HourglassEmpty,
                        contentDescription = "Expired",
                        tint = Color.White,
                        modifier = Modifier.size(size * 0.3f)
                    )
                }
            }
        }
    }
}

@Composable
fun PromotionBusinessLogo(
    businessName: String?,
    promotion: PromotionResponse,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 120.dp
) {
    val isExpired = isPromotionExpired(promotion)
    val daysLeft = getDaysUntilExpiration(promotion)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        BusinessLogo(
            businessName = businessName,
            size = size,
            isExpired = isExpired,
            showExpiredOverlay = true,
            shape = null // No shape - raw image
        )

        // Status indicator
        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = when {
                isExpired -> Color.Red.copy(alpha = 0.2f)
                daysLeft <= 3 -> Color(0xFFFF9800).copy(alpha = 0.2f)
                else -> Color.Green.copy(alpha = 0.2f)
            }
        ) {
            Text(
                text = when {
                    isExpired -> "EXPIRED"
                    daysLeft <= 3 -> "ENDING SOON"
                    else -> "ACTIVE"
                },
                color = when {
                    isExpired -> Color.Red
                    daysLeft <= 3 -> Color(0xFFFF9800)
                    else -> Color.Green
                },
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}