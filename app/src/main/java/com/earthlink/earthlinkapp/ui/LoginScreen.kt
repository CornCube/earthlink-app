package com.earthlink.earthlinkapp.ui
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.material.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavController
import com.earthlink.earthlinkapp.data.PreferencesKeys
import com.earthlink.earthlinkapp.model.LoginData
import com.earthlink.earthlinkapp.network.login
import com.earthlink.earthlinkapp.network.validateToken
import com.earthlink.earthlinkapp.utils.Screen
import com.earthlink.earthlinkapp.utils.getUserFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LoginScreen(navController: NavController, dataStore: DataStore<Preferences>) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    fun filterValidCharacters(input: String): String {
        return input.filter { it.isLetterOrDigit() || it in listOf('_', '-', '@', '.', '!', '#', '$', '%', '&', '*', '+') }
    }

    val isLoginEnabled = email.isNotBlank() && password.isNotBlank()

    val userFlow = getUserFlow(dataStore)
    val user by userFlow.collectAsState(initial = "")

    val colors = MaterialTheme.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = filterValidCharacters(it) },
            label = { Text("Email", color = colors.onSurface) },
            textStyle = TextStyle(color = colors.onSurface),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = colors.onSurface,
                focusedBorderColor = colors.primary,
                unfocusedBorderColor = colors.onSurface.copy(alpha = ContentAlpha.medium)
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = filterValidCharacters(it) },
            label = { Text("Password") },
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
            loading = true
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = login(LoginData(email, password))
                    response?.let {
                        val token = it.token
                        val userID = validateToken(token)
                        Log.d("user", userID.toString().substringAfter("userID=").substringBefore(")"))
                        dataStore.edit { preferences ->
                            preferences[PreferencesKeys.USER_ID_KEY] = userID.toString().substringAfter("userID=").substringBefore(")")
                        }
                        withContext(Dispatchers.Main) {
                            //MAKE SURE TO RREMEMBER THIS USERID
                            if (userID != null) {
                                navController.navigate(Screen.Home.route)
                            } else {
                                errorMessage = "Login Failed"
                            }
                        }
                    } ?: run {
                        withContext(Dispatchers.Main) {
                            errorMessage = "Login failed: Incorrect credentials"
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        errorMessage = "Error: ${e.localizedMessage}"
                    }
                } finally {
                    withContext(Dispatchers.Main) {
                        loading = false
                    }
                }
            }
        }, enabled = isLoginEnabled) {
            Text("Login")
        }
        Button(onClick = {
            navController.navigate("signup")
        }){
            Text("Sign Up")
        }
    }
}