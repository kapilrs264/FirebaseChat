package com.app.firebasegroupchat

data class Message(
    val text: String = "",
    val senderId: String = "",
    val timestamp: Long = 0L,
    val groupId: String = "" // New field to group messages
)

data class Group(
    var id: String = "",
    var groupName: String = "",
    var groupDescription: String = "",
    var adminId: String = "",
    var members: List<String> = emptyList()
)
