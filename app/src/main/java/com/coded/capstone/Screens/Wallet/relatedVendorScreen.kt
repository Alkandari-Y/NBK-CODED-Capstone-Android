@file:OptIn(ExperimentalMaterial3Api::class)
package com.coded.capstone.screens.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.coded.capstone.ui.AppBackground
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

    AppBackground {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (perkCategories.isNotEmpty()) {
                                "${perkCategories.joinToString(" & ")} Vendors"
                            } else {
                                "Related Vendors"
                            },
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { 
                            navController.navigate(NavRoutes.homeWithWalletTab()) {
                                popUpTo(NavRoutes.NAV_ROUTE_HOME) { inclusive = true }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                if (isLoading) {
                    // Loading state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
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
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Unable to find the selected perk",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                } else {
                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { 
                            Text(
                                "Search vendors...", 
                                color = Color.White.copy(alpha = 0.6f)
                            ) 
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF8EC5FF),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedContainerColor = Color.White.copy(alpha = 0.15f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.15f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    // Perk Info Section
                    currentPerk?.let { perk ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF2A2A2A)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Perk: ${perk.type?.replaceFirstChar { it.uppercase() } ?: "Unknown"}",
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
                            color = Color.White.copy(alpha = 0.8f),
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
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (searchQuery.isNotBlank()) {
                                        "Try adjusting your search"
                                    } else {
                                        "No vendors available for this perk"
                                    },
                                    color = Color.White.copy(alpha = 0.7f),
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
}

@Composable
fun VendorListItem(partner: PartnerDto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle vendor click if needed */ },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Vendor Logo
            AsyncImage(
                model = partner.logoUrl,
                contentDescription = partner.name,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.1f)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Vendor Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Category Tag
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF8EC5FF).copy(alpha = 0.2f)
                ) {
                    Text(
                        partner.category.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        color = Color(0xFF8EC5FF),
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Text(
                    partner.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    "Available for this perk",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

