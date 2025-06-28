@file:OptIn(ExperimentalMaterial3Api::class)
package com.coded.capstone.screens.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.coded.capstone.data.requests.partner.PartnerDto
import com.coded.capstone.data.responses.perk.PerkDto
import com.coded.capstone.viewModels.HomeScreenViewModel
import com.coded.capstone.viewModels.RecommendationViewModel
import com.coded.capstone.navigation.NavRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelatedVendorsScreen(
    navController: NavController,
    perkId: String,
    productId: String,
    accountId: String,
    homeViewModel: HomeScreenViewModel,
    recommendationViewModel: RecommendationViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    val partners by recommendationViewModel.partners.collectAsState()
    val perks by homeViewModel.perksOfAccountProduct.collectAsState()
    val context = LocalContext.current

    // State for loading
    var isLoading by remember { mutableStateOf(true) }

    // Fetch data when screen loads
    LaunchedEffect(Unit) {
        println("Fetching perks and partners...")
        try {
            // Fetch both perks and partners
            homeViewModel.fetchPerksOfAccountProduct(productId)
            recommendationViewModel.fetchBusinessPartners()
            isLoading = false
        } catch (e: Exception) {
            println("Error fetching data: ${e.message}")
            isLoading = false
        }
    }

    // Find the perk by ID
    val currentPerk = remember(perkId, perks) {
        println("Looking for perk with ID: $perkId")
        println("Available perks: ${perks.map { "ID: ${it.id}, Type: ${it.type}, Categories: ${it.categories?.map { cat -> cat.name }}" }}")
        val foundPerk = perks.find { it.id?.toString() == perkId }
        println("Found perk: ${foundPerk?.type}, ID: ${foundPerk?.id}, Categories: ${foundPerk?.categories?.map { it.name }}")
        foundPerk
    }

    // Get categories from the perk
    val perkCategories = remember(currentPerk) {
        val categories = currentPerk?.categories?.mapNotNull { it.name } ?: emptyList()
        println("Extracted perk categories: $categories")
        categories
    }

    // Filter partners by perk categories and search query
    val filteredPartners = remember(partners, perkCategories, searchQuery) {
        println("Starting filtering with ${partners.size} total partners")
        println("Looking for partners matching any of these categories: $perkCategories")
        
        val filtered = partners.filter { partner ->
            val matchesCategory = if (perkCategories.isEmpty()) {
                println("No perk categories found, no partners will be shown")
                false
            } else {
                val matches = perkCategories.any { category ->
                    val isMatch = partner.category.name.equals(category, ignoreCase = true)
                    println("Checking partner '${partner.name}' (category: ${partner.category.name}) against perk category '$category': $isMatch")
                    isMatch
                }
                println("Partner '${partner.name}' final match result: $matches")
                matches
            }
            
            val matchesSearch = searchQuery.isBlank() || 
                partner.name.contains(searchQuery, ignoreCase = true) ||
                partner.category.name.contains(searchQuery, ignoreCase = true)
            
            val finalResult = matchesCategory && matchesSearch
            println("Partner '${partner.name}' final inclusion: $finalResult (category match: $matchesCategory, search match: $matchesSearch)")
            finalResult
        }
        
        println("Final filtered partners count: ${filtered.size}")
        filtered
    }

    // Debug print for partners
    LaunchedEffect(partners) {
        println("Partners updated: ${partners.size} partners available")
        partners.forEach { partner ->
            println("Partner: ${partner.name}, Category: ${partner.category.name}")
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White, // Light background
        topBar = {
                            TopAppBar(
                title = {
                    Text(
                        text = "Available at",
                        color = Color(0xFF1E1E1E),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { 
                            navController.navigate(NavRoutes.homeWithWalletTab()) {
                                popUpTo(NavRoutes.NAV_ROUTE_HOME) { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .size(40.dp)
                            .background(
                                Color(0xFF8EC5FF).copy(alpha = 0.1f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF8EC5FF),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF1E1E1E),
                    navigationIconContentColor = Color(0xFF8EC5FF)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .background(Color.White)
        ) {
            if (isLoading) {
                // Loading state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF8EC5FF))
                }
            } else if (currentPerk == null) {
                // Error state - Perk not found
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Perk Not Found",
                            color = Color(0xFF1E1E1E),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Unable to find the selected perk",
                            color = Color(0xFF6D6D6D),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            } else {
                // Add top spacing
                Spacer(modifier = Modifier.height(16.dp))
                
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { 
                        Text(
                            "Search vendors...", 
                            color = Color(0xFF8E8E93)
                        ) 
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = Color(0xFF8EC5FF)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF8EC5FF),
                        unfocusedBorderColor = Color(0xFFE5E5E5),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = Color(0xFF1E1E1E),
                        unfocusedTextColor = Color(0xFF1E1E1E)
                    )
                )

                // Perk Info Section
                currentPerk?.let { perk ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF374151)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = perk.type?.replaceFirstChar { it.uppercase() } ?: "Unknown",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            perk.perkAmount?.let { amount ->
                                Text(
                                    text = when {
                                        perk.type?.contains("cashback", ignoreCase = true) == true -> "$amount% Cashback"
                                        perk.type?.contains("discount", ignoreCase = true) == true -> "$amount% Discount"
                                        else -> "$amount Points"
                                    },
                                    color = Color(0xFF8EC5FF),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            if (perkCategories.isNotEmpty()) {
                                Text(
                                    text = "Categories: ${perkCategories.joinToString(", ")}",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Vendors List
                if (filteredPartners.isNotEmpty()) {
                    Text(
                        text = "${filteredPartners.size} vendor${if (filteredPartners.size != 1) "s" else ""} found",
                        color = Color(0xFF6D6D6D),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredPartners) { partner ->
                            VendorListItem(partner = partner)
                        }
                        
                        // Bottom padding
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                } else {
                    // No vendors found
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No vendors found",
                                color = Color(0xFF1E1E1E),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (searchQuery.isNotBlank()) {
                                    "Try adjusting your search"
                                } else {
                                    "No vendors available for this category"
                                },
                                color = Color(0xFF6D6D6D),
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VendorListItem(partner: PartnerDto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle vendor click */ },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F9FA)
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Vendor logo placeholder
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF8EC5FF)),
                contentAlignment = Alignment.Center
            ) {
                if (!partner.logoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = partner.logoUrl,
                        contentDescription = "Vendor Logo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Default blue background with white text
                    Text(
                        text = partner.name.firstOrNull()?.toString()?.uppercase() ?: "?",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Vendor details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = partner.name,
                    color = Color(0xFF1E1E1E),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = partner.category.name,
                    color = Color(0xFF8EC5FF),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
 
            }
        }
    }
}

