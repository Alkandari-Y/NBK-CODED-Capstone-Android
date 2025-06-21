package com.coded.capstone.wallet.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.coded.capstone.wallet.data.WalletAccountDisplayModel



@Composable
fun QuickActionButtons(
    selectedAccount: WalletAccountDisplayModel,
    allAccounts: List<WalletAccountDisplayModel>,
    onPayClick: () -> Unit, // TODO: NFC payment implementation
    onTopUpClick: () -> Unit,
    onTransferClick: () -> Unit,
    onViewDetailsClick: () -> Unit, // TODO: Navigate to account details
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // Pay Button - TODO: NFC Payment
        QuickActionButton(
            icon = Icons.Outlined.Payment,
            label = "Pay",
            onClick = onPayClick, // TODO: Currently does nothing
            enabled = selectedAccount.isActive,
            modifier = Modifier.weight(1f)
        )

        // Top Up Button - DISABLED for cashback/business cards (Outlined Icon)
        QuickActionButton(
            icon = Icons.Outlined.Add,
            label = "Top Up",
            onClick = onTopUpClick,
            enabled = selectedAccount.canTopUp && selectedAccount.isActive,
            modifier = Modifier.weight(1f)
        )

        // Transfer Button
        QuickActionButton(
            icon = Icons.Outlined.SwapHoriz,
            label = "Transfer",
            onClick = onTransferClick,
            enabled = selectedAccount.canTransfer && selectedAccount.isActive,
            modifier = Modifier.weight(1f)
        )

        // Details Button - TODO: Account Details Navigation
        QuickActionButton(
            icon = Icons.Outlined.Info,
            label = "Details",
            onClick = onViewDetailsClick, // TODO: Currently does nothing
            enabled = true,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) Color(0xFF4CAF50) else Color.Gray,
            contentColor = if (enabled) Color.White else Color.DarkGray,
            disabledContainerColor = Color.Gray.copy(alpha = 0.6f),
            disabledContentColor = Color.DarkGray
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(20.dp),
                tint = if (enabled) Color.White else Color.DarkGray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (enabled) Color.White else Color.DarkGray
            )
        }
    }
}
