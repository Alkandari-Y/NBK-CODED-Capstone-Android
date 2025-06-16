package com.coded.capstone.Calender.calendar.offers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.coded.capstone.data.Tmp.Offer

@Composable
fun OfferItem(
    offer: Offer,
    onClick: () -> Unit
) {
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
                .clickable(onClick = onClick),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A1A)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
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
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = when (offer) {
                            is Offer.SingleDate -> offer.category
                            is Offer.DateRange -> offer.category
                        },
                        color = Color(0xFFBDBDBD),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = when (offer) {
                        is Offer.SingleDate -> offer.description
                        is Offer.DateRange -> offer.description
                    },
                    color = Color(0xFFE0E0E0),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = when (offer) {
                        is Offer.SingleDate -> "Valid until: ${offer.date}"
                        is Offer.DateRange -> "Valid from: ${offer.startDate} to ${offer.endDate}"
                    },
                    color = Color(0xFFBDBDBD),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
} 