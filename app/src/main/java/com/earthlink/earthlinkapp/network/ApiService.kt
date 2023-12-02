package com.earthlink.earthlinkapp.network

import com.earthlink.earthlinkapp.model.LoginData
import com.earthlink.earthlinkapp.model.LoginResponse
import com.earthlink.earthlinkapp.model.Message
import com.earthlink.earthlinkapp.model.MessageListFormat
import com.earthlink.earthlinkapp.model.PostMessageResponse
import com.earthlink.earthlinkapp.model.SignUpData
import com.earthlink.earthlinkapp.model.SignUpResponse
import com.earthlink.earthlinkapp.model.ValidateResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("/getMessagesByRadius/{latitude}/{longitude}/{max_number}/{sort_type}")
    fun getMessagesRadius(@Path("latitude") latitude: Double, @Path("longitude") longitude: Double, @Path("max_number") max_number: Int, @Path("sort_type") sort_type: Int,): Call<List<List<MessageListFormat>>>

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

//    @POST("/changeReactions")
//    suspend fun changeReactions(@Body message: Message): Response<>
}
