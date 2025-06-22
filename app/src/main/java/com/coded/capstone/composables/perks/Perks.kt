package com.coded.capstone.composables.perks
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp





@Composable
fun CategoryIndicator(
    category: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = Color(0xFF374151),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Category indicator dot
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(
                    color = getCategoryColor(category),
                    shape = CircleShape
                )
        )

        Text(
            text = category,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFFE5E7EB),
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}

// Helper function to get category-specific colors
fun getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "food", "restaurant", "dining" -> Color(0xFFEF4444)
        "shopping", "retail" -> Color(0xFF8B5CF6)
        "fuel", "gas", "petrol" -> Color(0xFF10B981)
        "travel", "hotel" -> Color(0xFF3B82F6)
        "entertainment" -> Color(0xFFF59E0B)
        "groceries" -> Color(0xFF06B6D4)
        else -> Color(0xFF6B7280)
    }
}

// Keep existing helper functions
fun getPerkColor(type: String?): Color {
    return when (type?.lowercase()) {
        "cashback" -> Color(0xFF059669)
        "discount" -> Color(0xFF7C3AED)
        "points" -> Color(0xFFD97706)
        "rewards" -> Color(0xFFDC2626)
        else -> Color(0xFF2563EB)
    }
}

fun getPerkIcon(type: String?): androidx.compose.ui.graphics.vector.ImageVector {
    return when (type?.lowercase()) {
        "cashback" -> Icons.Default.AttachMoney
        "discount" -> Icons.Default.LocalOffer
        "points", "rewards" -> Icons.Default.Star
        else -> Icons.Default.CardGiftcard
    }
}




