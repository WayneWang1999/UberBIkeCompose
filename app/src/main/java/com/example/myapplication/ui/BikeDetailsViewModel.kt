package com.example.myapplication.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Bike
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class BikeDetailsViewModel(
    private val bikeReportViewModel: BikeReportViewModel
) : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _bikeState = MutableStateFlow<Bike?>(null)
    val bikeState: StateFlow<Bike?> = _bikeState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> get() = _errorState.asStateFlow()

    fun updateBikeReturned(bike: Bike) {
        // Update state locally
        _bikeState.update { bike.copy(isReturned = true) }

        // Perform FireStore update asynchronously
        viewModelScope.launch {
            try {
                val bikeId = getBikeIdByName(bike.bikeName)
                bikeId?.let {
                    firestore.collection("bikes")
                        .document(it)
                        .update("returned", true)
                        .addOnSuccessListener {
                            bikeReportViewModel.fetchBikeLocations(false)
                        }
                        .addOnFailureListener { exception ->
                            Log.e("BikeViewModel", "Error updating bike return status: $exception")
                            _errorState.value = "Failed to update bike return status."
                        }
                } ?: run {
                    Log.e("BikeViewModel", "Bike not found with name: ${bike.bikeName}")
                    _errorState.value = "Bike not found: ${bike.bikeName}"
                }
            } catch (e: Exception) {
                Log.e("BikeViewModel", "Error during update: $e")
                _errorState.value = "An unexpected error occurred."
            }
        }
    }

    private suspend fun getBikeIdByName(bikeName: String): String? {
        return try {
            val querySnapshot = firestore.collection("bikes")
                .whereEqualTo("bikeName", bikeName)
                .get()
                .await() // Converts to a suspendable function
            querySnapshot.documents.firstOrNull()?.id
        } catch (e: Exception) {
            Log.e("BikeViewModel", "Error fetching bike ID: $e")
            _errorState.value = "Error fetching bike ID."
            null
        }
    }

}
