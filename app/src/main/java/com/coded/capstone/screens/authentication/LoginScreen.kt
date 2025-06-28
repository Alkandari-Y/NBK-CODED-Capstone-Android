package com.coded.capstone.screens.authentication

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.coded.capstone.R
import com.coded.capstone.viewModels.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.coded.capstone.navigation.NavRoutes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.coded.capstone.formstates.authentication.LoginFormState
import com.coded.capstone.viewModels.AuthUiState
import kotlinx.coroutines.delay
import com.coded.capstone.ui.AppBackground

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = viewModel(),
    navController: NavHostController,
) {
    val uiState = viewModel.uiState.value
    val context = LocalContext.current
    val token = viewModel.token.value

    var showPassword by remember { mutableStateOf(false) }
    var formState by remember { mutableStateOf(LoginFormState()) }

    // Animation states
    var cardVisible by remember { mutableStateOf(false) }
    val cardOffsetY by animateDpAsState(
        targetValue = if (cardVisible) 140.dp else 800.dp,
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
        delay(300) // Small delay before starting animation
        cardVisible = true
    }

    LaunchedEffect(token) {
        if (token?.access?.isNotBlank() == true) {
            navController.navigate(NavRoutes.NAV_ROUTE_LOADING_DASHBOARD) {
                popUpTo(NavRoutes.NAV_ROUTE_LOGIN) { inclusive = true }
            }
        }
    }

    AppBackground {
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xFF374151))
        ) {
            // Logo in top section
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .offset(y = 32.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(15.dp)
            ) {
                // Use actual KLUE logo from drawable
                Image(
                    painter = painterResource(id = R.drawable.klue),
                    contentDescription = "KLUE Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Animated Login Card - Full Width
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
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White,
                                    Color(0xFFE5E7EB), // Light silver
                                    Color(0xFFD1D5DB)  // Silver
                                )
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp, vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Welcome",
                            style = TextStyle(
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF374151)
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = "Sign in to continue",
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = Color(0xFF6B7280)
                            ),
                            modifier = Modifier.padding(bottom = 40.dp)
                        )

                        // Email Field with subtle animation
                        OutlinedTextField(
                            value = formState.username,
                            onValueChange = { formState = formState.copy(username = it) },
                            label = {
                                Text(
                                    "Username",
                                    color = Color(0xFF6B7280),
                                    fontSize = 14.sp
                                )
                            },
                            placeholder = {
                                Text(
                                    "Enter your username",
                                    color = Color(0xFF9CA3AF),
                                    fontSize = 14.sp
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp)
                                .animateContentSize(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedContainerColor = Color(0xFFECECEC),
                                focusedContainerColor = Color(0xFFECECEC),
                                unfocusedTextColor = Color(0xFF374151),
                                focusedTextColor = Color(0xFF374151),
                                cursorColor = Color(0xFF374151)
                            ),
                            isError = formState.usernameError != null,
                            supportingText = {
                                formState.usernameError?.let {
                                    Text(text = it, color = Color.Red, fontSize = 12.sp)
                                }
                            }
                        )

                        // Password Field with subtle animation
                        OutlinedTextField(
                            value = formState.password,
                            onValueChange = {
                                formState = formState.copy(password = it).validate()
                            },
                            label = {
                                Text(
                                    "Password",
                                    color = Color(0xFF6B7280),
                                    fontSize = 14.sp
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 32.dp)
                                .animateContentSize(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedContainerColor = Color(0xFFEDEEEF),
                                focusedContainerColor = Color(0xFFE5E5E5),
                                unfocusedTextColor = Color(0xFF374151),
                                focusedTextColor = Color(0xFF374151),
                                cursorColor = Color(0xFF374151)
                            ),
                            isError = formState.passwordError != null,
                            supportingText = {
                                formState.passwordError?.let {
                                    Text(text = it, color = Color.Red, fontSize = 12.sp)
                                }
                            },
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle password visibility",
                                        tint = Color(0xFF6B7280)
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )

                        // Error message with animation
                        AnimatedVisibility(
                            visible = uiState is AuthUiState.Error,
                            enter = fadeIn() + slideInVertically(),
                            exit = fadeOut() + slideOutVertically()
                        ) {
                            if (uiState is AuthUiState.Error) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 20.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.Red.copy(alpha = 0.1f)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = uiState.message,
                                        color = Color.Red,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                        }

                        // Login Button with press animation
                        var buttonPressed by remember { mutableStateOf(false) }
                        val buttonScale by animateFloatAsState(
                            targetValue = if (buttonPressed) 0.95f else 1f,
                            animationSpec = tween(100),
                            label = "button_press"
                        )

                        Button(
                            onClick = {
                                val validated = formState.validate()
                                if (validated.formIsValid) {
                                    formState = validated
                                    viewModel.login(
                                        username = validated.username,
                                        password = validated.password
                                    )
                                } else {
                                    formState = validated
                                    Toast.makeText(context, "Fix errors before submitting", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 32.dp)
                                .scale(buttonScale)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = {
                                            buttonPressed = true
                                            tryAwaitRelease()
                                            buttonPressed = false
                                        }
                                    )
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF8EC5FF),
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 4.dp,
                                pressedElevation = 8.dp
                            )
                        ) {
                            Text(
                                text = "Log In",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Sign up link
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(color = Color(0xFF6B7280))) {
                                    append("Don't have an account? ")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        color = Color(0xFF8EC5FF),
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append("Register")
                                }
                            },
                            fontSize = 14.sp,
                            modifier = Modifier
                                .clickable {
                                    navController.navigate(NavRoutes.NAV_ROUTE_SIGNUP)
                                }
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}