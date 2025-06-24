@file:OptIn(ExperimentalMaterial3Api::class)
package com.coded.capstone.screens.kyc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.coded.capstone.composables.ui.profile.PersonalInformationSection
import com.coded.capstone.composables.ui.profile.ProfileHeader
import com.coded.capstone.composables.ui.profile.TierProgressSection
import com.coded.capstone.composables.ui.profile.UserInfoSection
import com.coded.capstone.respositories.UserRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onNavItemClick: (String) -> Unit = {}
) {
    val kyc = UserRepository.kyc

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Header
        ProfileHeader(
            onBackClick = onBackClick,
            onSettingsClick = onSettingsClick
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            // User Info Section
            item {
                UserInfoSection(
                    userProfile = kyc,
                    onEditClick = onEditClick
                )
            }

            // Tier Progress Section
            item {
                TierProgressSection()
            }

            // Personal Information Section
            item {
                PersonalInformationSection(
                    userProfile = kyc,
                    onEditClick = onEditClick
                )
            }

            item {
                Spacer(modifier = Modifier.height(100.dp)) // Space for bottom nav
            }
        }


    }
}











