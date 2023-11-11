package com.example.earthlink.network

import com.example.earthlink.model.Message
import com.example.earthlink.model.MessageListFormat
import com.example.earthlink.model.PostMessageResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("/getAllMessages")
    suspend fun getMessagesRadius(latitude: Double, longitude: Double): Call<Map<String, MessageListFormat>>

    @POST("/message")
    suspend fun postMessage(@Body message: Message): Call<PostMessageResponse>
}
