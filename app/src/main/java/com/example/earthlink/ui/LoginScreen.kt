package com.example.earthlink.ui
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.material.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavController
import com.example.earthlink.data.PreferencesKeys
import com.example.earthlink.model.LoginData
import com.example.earthlink.network.login
import com.example.earthlink.network.validateToken
import com.example.earthlink.utils.Screen
import com.example.earthlink.utils.getUserFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = filterValidCharacters(it)  },
            label = { Text("Email") }
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
                            errorMessage = "Login failed failed: Incorrect credentials"
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