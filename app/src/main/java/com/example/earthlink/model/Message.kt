package com.example.earthlink.model

data class Message(
    val longitude: Double,
    val latitude: Double,
    val message_content: String,
    val timeStamp: Long,
    val user_uid: String
)

// posting a message response
data class PostMessageResponse(
    val message: String,
    val message_id: String
)
