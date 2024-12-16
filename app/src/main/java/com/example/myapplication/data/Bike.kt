package com.example.myapplication.data


import com.google.firebase.firestore.DocumentId

data class Bike @JvmOverloads constructor(
    @DocumentId
    var bikeId:String = "",
    var bikeName:String="BIKE-001",
    val isReturned:Boolean=false,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val address: String = "",
    val description: String = "",
    val runTime: String = "20 mins"
)
