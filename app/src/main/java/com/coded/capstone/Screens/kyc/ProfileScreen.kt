

@file:OptIn(ExperimentalMaterial3Api::class)
package com.coded.capstone.screens.kyc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

data class UserProfile(
    val name: String,
    val email: String,
    val avatarUrl: String?,
    val currentTier: String,
    val nextTier: String,
    val currentXP: Int,
    val xpToNext: Int,
    val memberSince: String,
    val monthlySpending: Double,
    val accountAgeMonths: Int,
    val investmentPortfolio: Double
)

data class TierRequirement(
    val label: String,
    val isCompleted: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(
    // 🔥 REPLACE WITH YOUR VIEWMODEL OR REPOSITORY DATA
    userProfile: UserProfile = UserProfile(
        name = "Sarah Johnson",
        email = "sarah.johnson@email.com",
        avatarUrl = null,
        currentTier = "Gold",
        nextTier = "Platinum",
        currentXP = 7500,
        xpToNext = 2500,
        memberSince = "2023",
        monthlySpending = 2000.0,
        accountAgeMonths = 6,
        investmentPortfolio = 5000.0
    ),
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onNavItemClick: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        ProfileHeader(
            onBackClick = onBackClick,
            onSettingsClick = onSettingsClick
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            // User Info Section
            item {
                UserInfoSection(
                    userProfile = userProfile,
                    onEditClick = onEditClick
                )
            }

            // Tier Progress Section
            item {
                TierProgressSection(userProfile = userProfile)
            }

            // Personal Information Section
            item {
                PersonalInfoSection(onEditClick = onEditClick)
            }

            item {
                Spacer(modifier = Modifier.height(100.dp)) // Bottom nav space
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileHeader(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Profile",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        navigationIcon = {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

              ,
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.Gray
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}

@Composable
private fun UserInfoSection(
    userProfile: UserProfile,
    onEditClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.Gray.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            if (userProfile.avatarUrl != null) {
                AsyncImage(
                    model = userProfile.avatarUrl,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Default Avatar",
                    modifier = Modifier.size(40.dp),
                    tint = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Name
        Text(
            text = userProfile.name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Email with edit icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = userProfile.email,
                fontSize = 16.sp,
                color = Color.Gray
            )
            IconButton(
                onClick = onEditClick,
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Email",
                    modifier = Modifier.size(16.dp),
                    tint = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tier and Member info
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tier Badge
            Row(
                modifier = Modifier
                    .background(
                        Color.Black,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "👑",
                    fontSize = 16.sp
                )
                Text(
                    text = "${userProfile.currentTier} Tier",
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }

            Text(
                text = "Member since ${userProfile.memberSince}",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun TierProgressSection(userProfile: UserProfile) {
    Column {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tier Progress",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Info",
                modifier = Modifier.size(20.dp),
                tint = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Current vs Next tier
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Current: ${userProfile.currentTier}",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Text(
                text = "Next: ${userProfile.nextTier}",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Progress Bar
        val totalXP = userProfile.currentXP + userProfile.xpToNext
        val progress = userProfile.currentXP.toFloat() / totalXP.toFloat()

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = Color.Black,
            trackColor = Color.Gray.copy(alpha = 0.2f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // XP Info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${userProfile.currentXP.toString().replace(Regex("(\\d)(?=(\\d{3})+$)"), "$1,")} XP",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Text(
                text = "${userProfile.xpToNext.toString().replace(Regex("(\\d)(?=(\\d{3})+$)"), "$1,")} XP to go",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Requirements
        val requirements = listOf(
            TierRequirement(
                "Monthly spending: $${userProfile.monthlySpending.toInt().toString().replace(Regex("(\\d)(?=(\\d{3})+$)"), "$1,")}+",
                userProfile.monthlySpending >= 2000
            ),
            TierRequirement(
                "Account age: ${userProfile.accountAgeMonths}+ months",
                userProfile.accountAgeMonths >= 6
            ),
            TierRequirement(
                "Investment portfolio: $${userProfile.investmentPortfolio.toInt().toString().replace(Regex("(\\d)(?=(\\d{3})+$)"), "$1,")}+",
                userProfile.investmentPortfolio >= 5000
            )
        )

        requirements.forEach { requirement ->
            RequirementItem(requirement = requirement)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun RequirementItem(requirement: TierRequirement) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    if (requirement.isCompleted) Color.Black else Color.Gray.copy(alpha = 0.3f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (requirement.isCompleted) "✓" else "⏱",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = requirement.label,
            fontSize = 14.sp,
            color = if (requirement.isCompleted) Color.Black else Color.Gray
        )
    }
}

@Composable
private fun PersonalInfoSection(onEditClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Personal Information",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        IconButton(onClick = onEditClick) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Personal Info",
                modifier = Modifier.size(20.dp),
                tint = Color.Gray
            )
        }
    }
}