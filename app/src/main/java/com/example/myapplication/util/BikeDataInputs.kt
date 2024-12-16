package com.example.myapplication.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.example.myapplication.data.Bike
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

class BikeDataInputs (private val context: Context){
    private val db = FirebaseFirestore.getInstance()
    fun generateRandomBikeAndUpload() {
        val batch = db.batch() // Use batch writes for efficiency
        val bikesCollection = db.collection("bikes")

        for (i in 1..1) {
            val latitude = Random.nextDouble(43.66, 43.79)
            val longitude = Random.nextDouble(-79.51, -79.33)
            val bikeName = Random.nextInt(100, 999).toString()
            // Generate random data
            val bike = Bike(
                bikeName = "BIKE-${bikeName}",
                latitude = latitude,
                longitude = longitude,
                address = getStreetAddress(latitude, longitude),
                runTime = "${Random.nextInt(1, 100)} mins",
                description = "Ride is good in Toronto"
            )

            // Create a reference to a new document in the bikes collection
            val bikeRef = bikesCollection.document()

            // Update the bike object with the generated document ID
            val bikeWithId = bike.copy(bikeId = bikeRef.id)

            // Add the bike to the batch
            batch.set(bikeRef, bikeWithId)
        }

        // Commit the batch to Firestore
        batch.commit()
            .addOnSuccessListener {
                // Success callback
                Log.d("BikeViewModel", "Batch write succeeded")
            }
            .addOnFailureListener { e ->
                // Failure callback
                Log.e("BikeViewModel", "Batch write failed: $e")
            }
    }

    private fun getStreetAddress(latitude: Double, longitude: Double): String {
        // TODO: instantiate the geocoder class
        val geocoder = Geocoder(context)
        return try {
            // Retrieve location results using Geocoder
            val searchResults:MutableList<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            // Handle case where no results are found
            if (searchResults.isNullOrEmpty()) {
                return "No address found for the given location."
            }
            // Extract address details from the result
            val foundLocation: Address = searchResults[0]
            foundLocation.getAddressLine(0) ?: "Address not available"
        } catch (ex: Exception) {
            Log.e("TESTING", "Error while getting street address", ex)
            "Error while retrieving address: ${ex.localizedMessage}"
        }
    }

}