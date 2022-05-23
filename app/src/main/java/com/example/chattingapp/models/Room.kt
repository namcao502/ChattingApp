package com.example.chattingapp.models

class Room(
    var roomId: String? = "",
    var name: String? = "",
    var listUserId: ArrayList<String>? = null
) {
    override fun toString(): String {
        return "Room(roomId=$roomId, name=$name, listUserId=$listUserId)"
    }
}