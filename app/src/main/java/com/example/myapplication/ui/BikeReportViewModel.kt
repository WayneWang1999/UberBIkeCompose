package com.example.myapplication.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Bike
import com.example.myapplication.util.BikeDataInputs
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BikeReportViewModel(application: Application) : AndroidViewModel(application) {

    private val firestore = FirebaseFirestore.getInstance()
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

    fun fetchBikeLocations(showReturnedOnly: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
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

                        if (isReturned != showReturnedOnly) continue

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

    fun generateAndUploadBike(context: Context) {
        val bikeDataInputs = BikeDataInputs(context)
        bikeDataInputs.generateRandomBikeAndUpload()
        fetchBikeLocations(false)
    }
}
