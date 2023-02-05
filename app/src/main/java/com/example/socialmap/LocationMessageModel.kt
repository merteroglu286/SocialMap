package com.example.socialmap

data class LocationMessageModel(
    var latitude : String = "",
    var longitude : String = "",
    var imageUrl : String = "",
    var uid : String = "",
    var message : String = "",
    var name : String = "",
    var status : String = "",
    var isVisible : Boolean = true,
)