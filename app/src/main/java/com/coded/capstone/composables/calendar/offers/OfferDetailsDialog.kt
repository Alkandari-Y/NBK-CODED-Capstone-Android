package com.coded.capstone.composables.calendar.offers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.coded.capstone.data.Tmp.Offer

@Composable
fun OfferDetailsDialog(
    offer: Offer,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF424242)
            ),
            shape = RoundedCornerShape(16.dp)
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
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = when (offer) {
                            is Offer.SingleDate -> offer.category
                            is Offer.DateRange -> offer.category
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFBDBDBD)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = when (offer) {
                        is Offer.SingleDate -> offer.description
                        is Offer.DateRange -> offer.description
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFFE0E0E0)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = when (offer) {
                        is Offer.SingleDate -> "Valid until: ${offer.date}"
                        is Offer.DateRange -> "Valid from: ${offer.startDate} to ${offer.endDate}"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFBDBDBD)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1E88E5)
                    )
                ) {
                    Text("Close")
                }
            }
        }
    }
} 