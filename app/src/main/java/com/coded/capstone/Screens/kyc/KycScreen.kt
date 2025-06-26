package com.coded.capstone.screens.kyc

import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.coded.capstone.navigation.NavRoutes
import com.coded.capstone.viewModels.KycViewModel
import com.coded.capstone.viewModels.UiStatus
import kotlinx.coroutines.delay
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.Button
import androidx.compose.material3.TextButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import com.coded.capstone.ui.AppBackground
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.zIndex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KycScreen(
    navController: NavHostController,
    viewModel: KycViewModel = viewModel()
) {
    val context = LocalContext.current
    val formState = viewModel.formState.value
    val isEditMode = viewModel.isEditMode.value
    val status by viewModel.status.collectAsState()

    // Date picker state
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // Success message state
    var showSuccessMessage by remember { mutableStateOf(false) }

    // Animation states
    var cardVisible by remember { mutableStateOf(false) }
    val cardOffsetY by animateDpAsState(
        targetValue = if (cardVisible) 40.dp else 800.dp,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "card_slide_up"
    )

    val cardAlpha by animateFloatAsState(
        targetValue = if (cardVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 600,
            delayMillis = 200,
            easing = FastOutSlowInEasing
        ),
        label = "card_fade_in"
    )

    // Trigger animation on composition
    LaunchedEffect(Unit) {
        delay(300)
        cardVisible = true
    }

    LaunchedEffect(status) {
        when (status) {
            is UiStatus.Success -> {
                showSuccessMessage = true
            }
            is UiStatus.Error -> {
                Toast.makeText(context, (status as UiStatus.Error).message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    AppBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Back button
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .offset(y = 40.dp)
                    .background(
                        Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Animated KYC Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxSize()
                    .offset(y = cardOffsetY)
                    .alpha(cardAlpha),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                shape = RoundedCornerShape(
                    topStart = 50.dp,
                    topEnd = 0.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                ),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 32.dp, vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isEditMode) "Edit Profile" else "Profile",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF374151)
                            ),
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        // First Name
                        OutlinedTextField(
                            value = formState.firstName,
                            onValueChange = {
                                viewModel.formState.value = formState.copy(firstName = it).validate()
                            },
                            label = { Text("First Name", color = Color(0xFF6B7280), fontSize = 14.sp) },
                            placeholder = { Text("Enter your first name", color = Color(0xFF9CA3AF), fontSize = 14.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            enabled = isEditMode,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedContainerColor = Color(0xFFF3F4F6),
                                focusedContainerColor = Color(0xFFF3F4F6),
                                unfocusedTextColor = Color(0xFF374151),
                                focusedTextColor = Color(0xFF374151),
                                unfocusedPlaceholderColor = Color(0xFF9CA3AF),
                                focusedPlaceholderColor = Color(0xFF9CA3AF),
                                cursorColor = Color(0xFF374151)
                            ),
                            isError = formState.firstNameError != null,
                            supportingText = {
                                formState.firstNameError?.let {
                                    Text(text = it, color = Color.Red, fontSize = 12.sp)
                                }
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = "First Name", tint = Color(0xFF6B7280))
                            }
                        )

                        // Last Name
                        OutlinedTextField(
                            value = formState.lastName,
                            onValueChange = {
                                viewModel.formState.value = formState.copy(lastName = it).validate()
                            },
                            label = { Text("Last Name", color = Color(0xFF6B7280), fontSize = 14.sp) },
                            placeholder = { Text("Enter your last name", color = Color(0xFF9CA3AF), fontSize = 14.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            enabled = isEditMode,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedContainerColor = Color(0xFFF3F4F6),
                                focusedContainerColor = Color(0xFFF3F4F6),
                                unfocusedTextColor = Color(0xFF374151),
                                focusedTextColor = Color(0xFF374151),
                                unfocusedPlaceholderColor = Color(0xFF9CA3AF),
                                focusedPlaceholderColor = Color(0xFF9CA3AF),
                                cursorColor = Color(0xFF374151)
                            ),
                            isError = formState.lastNameError != null,
                            supportingText = {
                                formState.lastNameError?.let {
                                    Text(text = it, color = Color.Red, fontSize = 12.sp)
                                }
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = "Last Name", tint = Color(0xFF6B7280))
                            }
                        )

                        // Date of Birth
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clickable(enabled = isEditMode) { showDatePicker = true }
                        ) {
                            OutlinedTextField(
                                value = formState.dateOfBirth,
                                onValueChange = { /* No-op to prevent any text input */ },
                                label = { Text("Date of Birth", color = Color(0xFF6B7280), fontSize = 14.sp) },
                                placeholder = { Text("DD-MM-YYYY", color = Color(0xFF9CA3AF), fontSize = 14.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = false,
                                readOnly = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedContainerColor = Color(0xFFF3F4F6),
                                    focusedContainerColor = Color(0xFFF3F4F6),
                                    unfocusedTextColor = Color(0xFF374151),
                                    focusedTextColor = Color(0xFF374151),
                                    unfocusedPlaceholderColor = Color(0xFF9CA3AF),
                                    focusedPlaceholderColor = Color(0xFF9CA3AF),
                                    cursorColor = Color(0xFF374151)
                                ),
                                isError = formState.dateOfBirthError != null,
                                supportingText = {
                                    formState.dateOfBirthError?.let {
                                        Text(text = it, color = Color.Red, fontSize = 12.sp)
                                    }
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.DateRange, contentDescription = "Date of Birth", tint = Color(0xFF6B7280))
                                },
                                trailingIcon = {
                                    if (isEditMode) {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = "Pick date",
                                            tint = Color(0xFF6B7280)
                                        )
                                    }
                                }
                            )
                        }

                        if (showDatePicker) {
                            DatePickerDialog(
                                onDismissRequest = { showDatePicker = false },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            datePickerState.selectedDateMillis?.let { millis ->
                                                val date = Date(millis)
                                                val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                                                viewModel.formState.value = formState.copy(dateOfBirth = formatter.format(date)).validate()
                                            }
                                            showDatePicker = false
                                        }
                                    ) {
                                        Text("OK")
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        onClick = { showDatePicker = false }
                                    ) {
                                        Text("Cancel")
                                    }
                                }
                            ) {
                                DatePicker(state = datePickerState)
                            }
                        }

                        // Salary
                        OutlinedTextField(
                            value = formState.salary,
                            onValueChange = {
                                viewModel.formState.value = formState.copy(salary = it).validate()
                            },
                            label = { Text("Monthly Salary", color = Color(0xFF6B7280), fontSize = 14.sp) },
                            placeholder = { Text("Enter your monthly salary", color = Color(0xFF9CA3AF), fontSize = 14.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            enabled = isEditMode,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedContainerColor = Color(0xFFF3F4F6),
                                focusedContainerColor = Color(0xFFF3F4F6),
                                unfocusedTextColor = Color(0xFF374151),
                                focusedTextColor = Color(0xFF374151),
                                unfocusedPlaceholderColor = Color(0xFF9CA3AF),
                                focusedPlaceholderColor = Color(0xFF9CA3AF),
                                cursorColor = Color(0xFF374151)
                            ),
                            isError = formState.salaryError != null,
                            supportingText = {
                                formState.salaryError?.let {
                                    Text(text = it, color = Color.Red, fontSize = 12.sp)
                                }
                            }
                        )

                        // Nationality
                        OutlinedTextField(
                            value = formState.nationality,
                            onValueChange = {
                                viewModel.formState.value = formState.copy(nationality = it).validate()
                            },
                            label = { Text("Nationality", color = Color(0xFF6B7280), fontSize = 14.sp) },
                            placeholder = { Text("Enter your nationality", color = Color(0xFF9CA3AF), fontSize = 14.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            enabled = isEditMode,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedContainerColor = Color(0xFFF3F4F6),
                                focusedContainerColor = Color(0xFFF3F4F6),
                                unfocusedTextColor = Color(0xFF374151),
                                focusedTextColor = Color(0xFF374151),
                                unfocusedPlaceholderColor = Color(0xFF9CA3AF),
                                focusedPlaceholderColor = Color(0xFF9CA3AF),
                                cursorColor = Color(0xFF374151)
                            ),
                            isError = formState.nationalityError != null,
                            supportingText = {
                                formState.nationalityError?.let {
                                    Text(text = it, color = Color.Red, fontSize = 12.sp)
                                }
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Public, contentDescription = "Nationality", tint = Color(0xFF6B7280))
                            }
                        )

                        // Mobile Number
                        OutlinedTextField(
                            value = formState.mobileNumber,
                            onValueChange = {
                                viewModel.formState.value = formState.copy(mobileNumber = it).validate()
                            },
                            label = { Text("Mobile Number", color = Color(0xFF6B7280), fontSize = 14.sp) },
                            placeholder = { Text("Enter your mobile number", color = Color(0xFF9CA3AF), fontSize = 14.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            enabled = isEditMode,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedContainerColor = Color(0xFFF3F4F6),
                                focusedContainerColor = Color(0xFFF3F4F6),
                                unfocusedTextColor = Color(0xFF374151),
                                focusedTextColor = Color(0xFF374151),
                                unfocusedPlaceholderColor = Color(0xFF9CA3AF),
                                focusedPlaceholderColor = Color(0xFF9CA3AF),
                                cursorColor = Color(0xFF374151)
                            ),
                            isError = formState.mobileNumberError != null,
                            supportingText = {
                                formState.mobileNumberError?.let {
                                    Text(text = it, color = Color.Red, fontSize = 12.sp)
                                }
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Phone, contentDescription = "Mobile Number", tint = Color(0xFF6B7280))
                            }
                        )

                        // Civil ID
                        OutlinedTextField(
                            value = formState.civilId,
                            onValueChange = {
                                viewModel.formState.value = formState.copy(civilId = it).validate()
                            },
                            label = { Text("Civil ID", color = Color(0xFF6B7280), fontSize = 14.sp) },
                            placeholder = { Text("Enter your civil ID", color = Color(0xFF9CA3AF), fontSize = 14.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 24.dp),
                            enabled = isEditMode,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedContainerColor = Color(0xFFF3F4F6),
                                focusedContainerColor = Color(0xFFF3F4F6),
                                unfocusedTextColor = Color(0xFF374151),
                                focusedTextColor = Color(0xFF374151),
                                unfocusedPlaceholderColor = Color(0xFF9CA3AF),
                                focusedPlaceholderColor = Color(0xFF9CA3AF),
                                cursorColor = Color(0xFF374151)
                            ),
                            isError = formState.civilIdError != null,
                            supportingText = {
                                formState.civilIdError?.let {
                                    Text(text = it, color = Color.Red, fontSize = 12.sp)
                                }
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Badge, contentDescription = "Civil ID", tint = Color(0xFF6B7280))
                            }
                        )

                        if (isEditMode) {
                            Button(
                                onClick = { viewModel.submitKyc() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                enabled = status !is UiStatus.Loading,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF8EC5FF),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                if (status is UiStatus.Loading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = Color.White
                                    )
                                } else {
                                    Text("Submit KYC", fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Success Message - positioned in the center of the screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1000f) // Ensure it appears above other elements
        ) {
            KycSuccessMessage(
                isVisible = showSuccessMessage,
                onDismiss = {
                    showSuccessMessage = false
                    navController.navigate(NavRoutes.NAV_ROUTE_CATEGORY_ONBOARDING) {
                        launchSingleTop = true
                    }
                },
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun KycSuccessMessage(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2E)),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = Color(0xFF8EC5FF),
                    modifier = Modifier.size(48.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "KYC Submitted Successfully!",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Your profile information has been updated and verified.",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8EC5FF),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Continue",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
