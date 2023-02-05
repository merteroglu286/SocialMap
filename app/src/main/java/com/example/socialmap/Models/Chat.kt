package com.example.socialmap.Models

data class Chat(
    var senderId:String = "",
    var receiverId:String = "",
    var message:String = "",
    var date: String = System.currentTimeMillis().toString(),
    )
