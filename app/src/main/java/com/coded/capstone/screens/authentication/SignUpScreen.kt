package com.coded.capstone.screens.authentication

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coded.capstone.R
import androidx.navigation.NavHostController
import com.coded.capstone.navigation.NavRoutes
import com.coded.capstone.viewModels.AuthUiState
import com.coded.capstone.viewModels.AuthViewModel
import androidx.compose.runtime.*
import com.coded.capstone.formstates.authentication.RegisterFormState


@Composable
fun SignUpScreen(
    viewModel: AuthViewModel = viewModel(),
    navController: NavHostController
) {
    val uiState = viewModel.uiState.value
    val fieldErrors = viewModel.registerFieldErrors.value
    val context = LocalContext.current

    var showPassword by remember { mutableStateOf(false) }
    var showConfirmedPassword by remember { mutableStateOf(false) }
    var formState by remember { mutableStateOf(RegisterFormState()) }

    LaunchedEffect(fieldErrors) {
        if (fieldErrors.isNotEmpty()) {
            formState = formState.applyServerErrors(fieldErrors)
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            Toast.makeText(context, "Account created", Toast.LENGTH_SHORT).show()
            navController.navigate(NavRoutes.NAV_ROUTE_LOADING_DASHBOARD) {
                popUpTo(NavRoutes.NAV_ROUTE_SIGNUP) { inclusive = true }
            }
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenHeight = maxHeight
        val screenWidth = maxWidth

        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Top Background Logo",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .height(screenHeight * 0.25f),
            contentScale = ContentScale.Fit
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Hi !", fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Text(
                text = "Welcome",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Let's create an account", fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = formState.username,
                onValueChange = { formState = formState.copy(username = it) },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                isError = formState.usernameError != null,
                supportingText = {
                    formState.usernameError?.let {
                        Text(text = it, color = Color.Red)
                    }
                }
            )

            OutlinedTextField(
                value = formState.email,
                onValueChange = { formState = formState.copy(email = it).validate() },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                isError = formState.usernameError != null,
                supportingText = {
                    formState.usernameError?.let {
                        Text(text = it, color = Color.Red)
                    }
                }
            )




            OutlinedTextField(
                value = formState.password,
                onValueChange = { formState = formState.copy(password = it).validate() },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                isError = formState.passwordError != null,
                supportingText = { formState.passwordError?.let { Text(it, color = Color.Red) } },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = "Toggle password visibility"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            OutlinedTextField(
                value = formState.confirmPassword,
                onValueChange = { formState = formState.copy(confirmPassword = it).validate() },
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                isError = formState.confirmPasswordError != null,
                supportingText = {
                    formState.confirmPasswordError?.let {
                        Text(it, color = Color.Red)
                    }
                },
                visualTransformation = if (showConfirmedPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showConfirmedPassword = !showConfirmedPassword }) {
                        Icon(
                            imageVector = Icons.Filled.Visibility,
                            contentDescription = "Toggle confirm password visibility"
                        )
                    }
                }
            )

            if (uiState is AuthUiState.Error) {
                Text(
                    text = uiState.message,
                    color = Color.Red,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }


            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    formState = formState.validate()
                    if (formState.formIsValid) {
                        viewModel.register(
                            username = formState.username,
                            email = formState.email,
                            password = formState.password
                        )
                    } else {
                        Toast.makeText(context, "Fix errors before submitting", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
            ) {
                Text(text = "Sign Up", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(text = "Have an account ? ", color = Color.Gray)
                Text(
                    text = "Log In",
                    color = Color(0xFF2196F3),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        navController.popBackStack(NavRoutes.NAV_ROUTE_LOGIN, false)
                    },
                )
            }
        }
    }
}