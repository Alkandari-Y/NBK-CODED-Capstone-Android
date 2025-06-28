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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import com.coded.capstone.composables.ui.profile.PersonalInformationSection
import com.coded.capstone.composables.ui.profile.ProfileHeader
import com.coded.capstone.composables.ui.profile.TierProgressSection
import com.coded.capstone.composables.ui.profile.UserInfoSection
import com.coded.capstone.respositories.UserRepository
import com.coded.capstone.viewModels.HomeScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onNavItemClick: (String) -> Unit = {}
) {
    val kyc = UserRepository.kyc
    val context = LocalContext.current
    val viewModel: HomeScreenViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return HomeScreenViewModel(context) as T
            }
        }
    )

    val userXp by viewModel.userXp.collectAsState()
    val xpTiers by viewModel.xpTiers.collectAsState()

    // Fetch XP info when screen loads
    LaunchedEffect(Unit) {
        viewModel.getUserXpInfo()
        viewModel.fetchXpTiers()
    }

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
                    userXp = userXp,
                    onEditClick = onEditClick
                )
            }

            // Tier Progress Section
            item {
                TierProgressSection(
                    userXp = userXp,
                    allTiers = xpTiers
                )
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











