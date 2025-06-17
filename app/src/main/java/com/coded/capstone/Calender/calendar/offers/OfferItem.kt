package com.coded.capstone.Calender.calendar.offers

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coded.capstone.data.Tmp.Offer

@Composable
fun OfferItem(
    offer: Offer,
    onClick: () -> Unit,
    isExpanded: Boolean = false,
    onToggleExpansion: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    // Use the passed isExpanded state if provided, otherwise use local state
    val isCurrentlyExpanded = if (onToggleExpansion != {}) isExpanded else expanded

    // Animate the card height with smoother animation
    val cardHeight by animateDpAsState(
        targetValue = if (isCurrentlyExpanded) 420.dp else 140.dp,
        animationSpec = tween(durationMillis = 500, easing = androidx.compose.animation.core.EaseInOutCubic),
        label = "cardHeight"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // Left border
        Box(
            modifier = Modifier
                .width(5.dp)
                .fillMaxHeight()
                .background(Color.White)
                .align(Alignment.CenterStart)
        )

        // Card content
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(cardHeight)
                .clickable {
                    if (onToggleExpansion != {}) {
                        onToggleExpansion()
                    } else {
                        expanded = !expanded
                    }
                },
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A1A)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header row with title, category, and expand/collapse icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (offer) {
                            is Offer.SingleDate -> offer.name
                            is Offer.DateRange -> offer.name
                        },
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = when (offer) {
                                is Offer.SingleDate -> offer.category
                                is Offer.DateRange -> offer.category
                            },
                            color = Color(0xFFBDBDBD),
                            style = MaterialTheme.typography.bodySmall
                        )

                        Icon(
                            imageVector = if (isCurrentlyExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isCurrentlyExpanded) "Collapse" else "Expand",
                            tint = Color(0xFFBDBDBD),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Description - shows full text when expanded, truncated when collapsed
                Text(
                    text = when (offer) {
                        is Offer.SingleDate -> offer.description
                        is Offer.DateRange -> offer.description
                    },
                    color = Color(0xFFE0E0E0),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = if (isCurrentlyExpanded) Int.MAX_VALUE else 2,
                    overflow = if (isCurrentlyExpanded) TextOverflow.Visible else TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Validity information
                Text(
                    text = when (offer) {
                        is Offer.SingleDate -> "Valid until: ${offer.date}"
                        is Offer.DateRange -> "Valid from: ${offer.startDate} to ${offer.endDate}"
                    },
                    color = Color(0xFFBDBDBD),
                    style = MaterialTheme.typography.bodySmall
                )

                // Additional details shown when expanded
                if (isCurrentlyExpanded) {
                    Spacer(modifier = Modifier.height(20.dp))

                    // Terms and conditions section
                    Text(
                        text = "Terms & Conditions:",
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "• Offer valid only on the specified date(s)\n• Cannot be combined with other promotions\n• Subject to availability\n• Store reserves the right to modify terms\n• No cash value or exchange for other offers\n• Valid only at participating locations",
                        color = Color(0xFFE0E0E0),
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // How to redeem section
                    Text(
                        text = "How to Redeem:",
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "• Present this offer at participating locations\n• Show valid ID if required\n• Follow store-specific redemption process\n• Contact store for more details\n• Keep this offer handy for reference\n• Check store hours before visiting",
                        color = Color(0xFFE0E0E0),
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Additional information section
                    Text(
                        text = "Additional Information:",
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "• This offer is part of our seasonal promotions\n• Limited time availability\n• Restrictions may apply based on location\n• Contact customer service for support\n• Follow us for more exclusive deals",
                        color = Color(0xFFE0E0E0),
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Contact information section
                    Text(
                        text = "Contact & Support:",
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "• Customer Service: 1-800-OFFERS\n• Email: support@offers.com\n• Live Chat: Available 24/7\n• Store Locator: Find nearest location\n• FAQ: Common questions answered",
                        color = Color(0xFFE0E0E0),
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tap to collapse indicator
                    Text(
                        text = "Tap to collapse",
                        color = Color(0xFF888888),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
} 