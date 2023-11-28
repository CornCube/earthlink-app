package com.earthlink.earthlinkapp.ui

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.earthlink.earthlinkapp.model.SignUpData
import com.earthlink.earthlinkapp.network.signUp
import kotlinx.coroutines.*

@Composable
fun SignUpScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    fun filterValidCharacters(input: String): String {
        return input.filter { it.isLetterOrDigit() || it in listOf('_', '-', '@', '.', '!', '#', '$', '%', '&', '*', '+') }
    }

    val isSignUpEnabled = email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Sign up for EarthLink!",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TextField components for email, password, and confirmPassword
        OutlinedTextField(
            value = email,
            onValueChange = { email = filterValidCharacters(it) },
            label = { Text("Email") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = filterValidCharacters(it) },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = filterValidCharacters(it) },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (loading) {
            CircularProgressIndicator()
        }

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red)
        }

        Button(onClick = {
            if (password != confirmPassword) {
                errorMessage = "Passwords do not match"
                return@Button
            }
            loading = true
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = signUp(SignUpData(email, password))
                    withContext(Dispatchers.Main) {
                        if (response != null) {
                            navController.navigate("login")
                        } else {
                            errorMessage = "Sign up failed: account already exists"
                        }
                        loading = false
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        errorMessage = "Error: ${e.localizedMessage}"
                        loading = false
                    }
                }
            }
        }, enabled = isSignUpEnabled,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        )
        {
            Text("Sign Up")
        }
        Button(
            onClick = { navController.navigate("login") },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        )
        {
            Text("Back to Login")
        }
    }
}