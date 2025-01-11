package com.app.firebasegroupchat

data class Grou(
    val groupId: String,
    val groupName: String,
    val groupDescription: String,
    val adminId: String // User ID of the admin
)

data class User(val id: String, val username: String)

data class Message(
    val senderId: String = "",    // User ID who sends the message
    val messageText: String = "", // The content of the message
    val timestamp: Long = System.currentTimeMillis() // Timestamp of the message (optional)
)


data class Group(
    val groupId: String = "",
    val groupName: String = "",
    val groupDescription: String = "",
    val adminId: String = "",
    val members: Map<String, String> = mapOf()  // Contains member user IDs and their role
)

