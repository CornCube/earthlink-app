package com.example.earthlink.network

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.earthlink.model.LoginData
import com.example.earthlink.model.LoginResponse
import com.example.earthlink.model.Message
import com.example.earthlink.model.MessageListFormat
import com.example.earthlink.model.PostMessageResponse
import com.example.earthlink.model.SignUpData
import com.example.earthlink.model.SignUpResponse
import com.example.earthlink.model.ValidateResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse

object RetrofitHelper {

    val baseUrl = "http://10.0.2.2:8000/"

    fun getInstance(): Retrofit {
        // Create a logging interceptor
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        // Create a custom OkHttpClient and add the logging interceptor
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client) // Add the client to Retrofit
            .build()
    }
}

suspend fun getMessagesRadius(latitude: Double, longitude: Double): List<MessageListFormat>? {
    val retrofit = RetrofitHelper.getInstance()
    val messagesService = retrofit.create(ApiService::class.java)

    return try {
        val response = messagesService.getMessagesRadius(latitude, longitude).awaitResponse()
        if (response.isSuccessful) {
            response.body()
        } else {
            null  // Or handle error response as appropriate
        }
    } catch (e: Exception) {
        // Log the exception or handle it as needed
        null
    }
}

suspend fun postMessage(message: Message): PostMessageResponse? {
    val retrofit = RetrofitHelper.getInstance()
    val messagesService = retrofit.create(ApiService::class.java)

    // Use a try-catch to handle potential exceptions
    return try {
        val response = messagesService.postMessage(message).awaitResponse()
        if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}

suspend fun signUp(userData: SignUpData): SignUpResponse? {
    val retrofit = RetrofitHelper.getInstance()
    val signUpService = retrofit.create(ApiService::class.java)

    return try {
        val response = signUpService.signUpUser(userData)
        if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    } catch (e : Exception){
        null
    }
}

suspend fun login(userData: LoginData): LoginResponse? {
    val retrofit = RetrofitHelper.getInstance()
    val loginService = retrofit.create(ApiService::class.java)

    return try {
        val response = loginService.loginUser(userData)
        if(response.isSuccessful){
            response.body()
        } else {
            null
        }
    } catch (e: Exception){
        null
    }
}

suspend fun validateToken(token: String): ValidateResponse? {
    val retrofit = RetrofitHelper.getInstance()
    val validateService = retrofit.create(ApiService::class.java)

    return try {
        val response = validateService.validateToken(token)
        if(response.isSuccessful){
            response.body()
        } else {
            null
        }
    } catch (e: Exception){
        null
    }
}