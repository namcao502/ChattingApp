package com.example.chattingapp.models

import com.google.firebase.Timestamp

class MessageInRoom(
    var messageId: String? = "",
    var userId: String? = "",
    var content: String? = "",
    var time: Timestamp? = null
)