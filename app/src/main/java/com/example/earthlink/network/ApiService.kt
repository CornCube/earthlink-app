package com.example.earthlink.network

import com.example.earthlink.model.Message
import com.example.earthlink.model.PostMessageResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("/getAllMessages")
    fun getAllMessages(): Call<List<Message>>

    @POST("/message")
    suspend fun postMessage(@Body message: Message): Call<PostMessageResponse>
}
