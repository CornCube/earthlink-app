package com.earthlink.earthlinkapp.model

data class Message(
    val longitude: Double,
    val latitude: Double,
    val message_content: String,
    val timestamp: Long,
    val user_uid: String,
    val likes: Int,
    val dislikes: Int
)

data class MessageListFormat(
    val geohash: String,
    val longitude: Double,
    val latitude: Double,
    val message_content: String,
    val timestamp: String,
    val user_uid: String,
    val likes: Int,
    val dislikes: Int
)

// posting a message response
data class PostMessageResponse(
    val message: String,
    val message_id: String
)
