package com.example.myapplication

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.data.Bike
import com.google.firebase.firestore.FirebaseFirestore

class BikeViewModel(application: Application) : AndroidViewModel(application) {

    private val firestore = FirebaseFirestore.getInstance()

    private val _bikeLocations = MutableLiveData<List<Bike>>()
    val bikeLocations: LiveData<List<Bike>> get() = _bikeLocations

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    init {
        // Initially, fetch "false" bikes (empty state or placeholder bikes)
        fetchBikeLocations(false)
    }

    fun updateBikeReturnStatus(updatedBike: Bike) {
        val db = FirebaseFirestore.getInstance()

        // Log the bikeName for debugging
        Log.d("BikeViewModel", "Bike Name: ${updatedBike.bikeName}   ${updatedBike.isReturned}")

        // Search for the bike document by bikeName//
        //********************************************************//
        db.collection("bikes")
            .whereEqualTo("bikeName", updatedBike.bikeName) // Find bike by bikeName
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Check if we found the bike
                if (!querySnapshot.isEmpty) {
                    // Assuming bikeName is unique, we get the first matching document
                    val bikeDocument = querySnapshot.documents[0]
                    val bikeId = bikeDocument.id // Get the document ID
                    Log.d("BikeViewModel", "bikeId: ${bikeId}  ")

                    // Now update the bike's returned status
                    db.collection("bikes")
                        .document(bikeId) // Use the document ID for the update
                        .update("returned", updatedBike.isReturned)
                        .addOnSuccessListener {
                            Log.d("BikeViewModel", "Bike return status updated successfully")
                            fetchBikeLocations(false)
                        }
                        .addOnFailureListener { exception ->
                            Log.e("BikeViewModel", "Error updating bike return status: $exception")
                        }
                } else {
                    Log.e("BikeViewModel", "No bike found with the name: ${updatedBike.bikeName}")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("BikeViewModel", "Error fetching bike by name: $exception")
            }
    }



    fun fetchBikeLocations(showReturnedOnly: Boolean?) {
        firestore.collection("bikes")
            .get()
            .addOnSuccessListener { documents ->
                val bikes = mutableListOf<Bike>()

                for (document in documents) {
                    val latitude = document.getDouble("latitude") ?: continue
                    val longitude = document.getDouble("longitude") ?: continue
                    val isReturned = document.getBoolean("returned") ?: false
                    val address = document.getString("address") ?: ""
                    val name = document.getString("bikeName") ?: "BIKE"

                    // Apply filter logic only if showReturnedOnly is not null
                    if (showReturnedOnly != null) {
                        if (isReturned != showReturnedOnly) continue
                    }

                    bikes.add(
                        Bike(
                        latitude = latitude,
                        longitude = longitude,
                        isReturned = isReturned,
                        address = address,
                        bikeName = name
                    )
                    )
                }

                _bikeLocations.postValue(bikes)
            }
            .addOnFailureListener { exception ->
                _errorMessage.postValue("Failed to load bike locations: ${exception.message}")
            }
    }

}
