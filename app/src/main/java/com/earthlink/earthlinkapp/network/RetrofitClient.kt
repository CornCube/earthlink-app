package com.earthlink.earthlinkapp.network

import com.earthlink.earthlinkapp.model.LoginData
import com.earthlink.earthlinkapp.model.LoginResponse
import com.earthlink.earthlinkapp.model.Message
import com.earthlink.earthlinkapp.model.MessageListFormat
import com.earthlink.earthlinkapp.model.NumMessagesResponse
import com.earthlink.earthlinkapp.model.PostMessageResponse
import com.earthlink.earthlinkapp.model.ReactionData
import com.earthlink.earthlinkapp.model.ReactionResponse
import com.earthlink.earthlinkapp.model.SignUpData
import com.earthlink.earthlinkapp.model.SignUpResponse
import com.earthlink.earthlinkapp.model.ValidateResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.awaitResponse

object RetrofitHelper {

    val baseUrl = "http://EarthLinkAPI.us-west-2.elasticbeanstalk.com:80/"
//    val baseUrl = "http://10.0.2.2:8000/"

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

suspend fun getMessagesFromUser(user_uid: String, sort_type: Int, search_term: String? = null): List<MessageListFormat>? {
    val retrofit = RetrofitHelper.getInstance()
    val messagesService = retrofit.create(ApiService::class.java)

    return try {
        val response = messagesService.getMessagesFromUser(user_uid, sort_type, search_term).awaitResponse()
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

suspend fun getMessagesRadius(latitude: Double, longitude: Double, max_number: Int, sort_type: Int): List<List<MessageListFormat>>? {
    val retrofit = RetrofitHelper.getInstance()
    val messagesService = retrofit.create(ApiService::class.java)

    return try {
        val response = messagesService.getMessagesRadius(latitude, longitude, max_number, sort_type).awaitResponse()
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

suspend fun deleteMessage(messageId: String): String? {
    val retrofit = RetrofitHelper.getInstance()
    val messagesService = retrofit.create(ApiService::class.java)

    // Use a try-catch to handle potential exceptions
    return try {
        val response = messagesService.deleteMessage(messageId).awaitResponse()
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

suspend fun changeReactions(reactionData: ReactionData): ReactionResponse? {
    val retrofit = RetrofitHelper.getInstance()
    val reactionService = retrofit.create(ApiService::class.java)

    return try {
        val response = reactionService.changeReactions(reactionData)
        if(response.isSuccessful){
            response.body()
        } else {
            null
        }
    } catch (e: Exception){
        null
    }
}

suspend fun getNumberMessages(user_uid: String): NumMessagesResponse? {
    val retrofit = RetrofitHelper.getInstance()
    val numberMessagesService = retrofit.create(ApiService::class.java)

    return try {
        val response = numberMessagesService.getNumberMessages(user_uid).awaitResponse()
        if(response.isSuccessful){
            response.body()
        } else {
            null
        }
    } catch (e: Exception){
        null
    }
}
