package com.example.myapplication.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.data.Bike
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BikeViewModel(application: Application) : AndroidViewModel(application) {

    private val firestore = FirebaseFirestore.getInstance()

    //***************Use LiveDta to define properties
//    private val _bikeLocations = MutableLiveData<List<Bike>>()
//    val bikeLocations: LiveData<List<Bike>> get() = _bikeLocations
//
//    private val _errorMessage = MutableLiveData<String>()
//    val errorMessage: LiveData<String> get() = _errorMessage
//
//    //move the state variables from the screen
//
//    private val _isFilterApplied = MutableLiveData(false)
//    val isFilterApplied: LiveData<Boolean> get() = _isFilterApplied
//
//    private val _selectedBike = MutableLiveData<Bike?>()
//    val selectedBike: LiveData<Bike?> get() = _selectedBike

    // Use StateFlow  to define properties
    private val _bikeLocations = MutableStateFlow<List<Bike>>(emptyList())
    val bikeLocations: StateFlow<List<Bike>> get() = _bikeLocations.asStateFlow()

    private val _isFilterApplied = MutableStateFlow(false)
    val isFilterApplied: StateFlow<Boolean> get() = _isFilterApplied.asStateFlow()

    private val _selectedBike = MutableStateFlow<Bike?>(null)
    val selectedBike: StateFlow<Bike?> get() = _selectedBike.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage.asStateFlow()

    init {
        fetchBikeLocations(false)
    }

    fun setFilterApplied(isApplied: Boolean) {
        _isFilterApplied.value = isApplied
        fetchBikeLocations(isApplied)
    }

    fun setSelectedBike(bike: Bike?) {
        _selectedBike.value = bike
    }

    fun updateBikeReturnStatus(updatedBike: Bike) {
        //Use stateflow add CoroutineScope to launch function.
        CoroutineScope(Dispatchers.IO).launch {
            firestore.collection("bikes")
                .whereEqualTo("bikeName", updatedBike.bikeName)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val bikeDocument = querySnapshot.documents[0]
                        val bikeId = bikeDocument.id
                        firestore.collection("bikes")
                            .document(bikeId)
                            .update("returned", updatedBike.isReturned)
                            .addOnSuccessListener {
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
    }

    fun fetchBikeLocations(showReturnedOnly: Boolean?) {
        CoroutineScope(Dispatchers.IO).launch {
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

                        if (showReturnedOnly != null && isReturned != showReturnedOnly) continue

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
                    _bikeLocations.value = bikes
                }
                .addOnFailureListener { exception ->
                    _errorMessage.value = "Failed to load bike locations: ${exception.message}"
                }
        }
    }
}
