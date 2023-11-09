package com.example.earthlink.network

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.earthlink.model.Message
import com.example.earthlink.model.PostMessageResponse
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

private val _messages = MutableLiveData<List<Message>>()
val messages: LiveData<List<Message>> = _messages

fun getMessages() {
    val retrofit = RetrofitHelper.getInstance()
    val messagesService = retrofit.create(ApiService::class.java)

    messagesService.getAllMessages().enqueue(object : Callback<List<Message>> {
        override fun onResponse(call: Call<List<Message>>, response: Response<List<Message>>) {
            if (response.isSuccessful) {
                _messages.postValue(response.body())
            } else {
                _messages.postValue(emptyList())
            }
        }

        override fun onFailure(call: Call<List<Message>>, t: Throwable) {
            _messages.postValue(emptyList())
        }
    })
}

// Make the function suspendable
suspend fun postMessage(message: Message): PostMessageResponse? {
    val retrofit = RetrofitHelper.getInstance()
    val messagesService = retrofit.create(ApiService::class.java)

    // Use a try-catch to handle potential exceptions
    return try {
        // Synchronously send the request and return the response
        val response = messagesService.postMessage(message).awaitResponse()
        if (response.isSuccessful) {
            response.body()
        } else {
            null // Or handle the error as you see fit
        }
    } catch (e: Exception) {
        null // Or log the exception
    }
}