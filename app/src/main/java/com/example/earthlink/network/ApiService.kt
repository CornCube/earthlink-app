package com.example.earthlink.network

import com.example.earthlink.model.LoginData
import com.example.earthlink.model.LoginResponse
import com.example.earthlink.model.Message
import com.example.earthlink.model.MessageListFormat
import com.example.earthlink.model.PostMessageResponse
import com.example.earthlink.model.SignUpData
import com.example.earthlink.model.SignUpResponse
import com.example.earthlink.model.ValidateResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("/getMessagesByRadius/{latitude}/{longitude}")
    fun getMessagesRadius(@Path("latitude") latitude: Double, @Path("longitude") longitude: Double): Call<List<MessageListFormat>>

    @GET("/getMessagesFromUser/{user_uid}")
    fun getMessagesFromUser(@Path("user_uid") user_uid: String): Call<Map<String, MessageListFormat>>

    @DELETE("/deleteMessage/{messageId}")
    fun deleteMessage(@Path("messageId") messageId: String): Call<String>

    @POST("/message")
    suspend fun postMessage(@Body message: Message): Call<PostMessageResponse>

    @POST("/signup")
    suspend fun signUpUser(@Body userData: SignUpData): Response<SignUpResponse>

    @POST("/login")
    suspend fun loginUser(@Body userData: LoginData): Response<LoginResponse>

    @POST("/ping")
    suspend fun validateToken(@Header("authorization") token: String): Response<ValidateResponse>


}
