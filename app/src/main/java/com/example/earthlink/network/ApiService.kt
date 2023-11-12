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
import retrofit2.http.Path

interface ApiService {
    @GET("/getMessagesByRadius/{latitude}/{longitude}")
    fun getMessagesRadius(@Path("latitude") latitude: Double, @Path("longitude") longitude: Double): Call<List<MessageListFormat>>
    @POST("/message")
    suspend fun postMessage(@Body message: Message): Call<PostMessageResponse>
}
