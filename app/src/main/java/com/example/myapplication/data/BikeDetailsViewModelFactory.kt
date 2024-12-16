package com.example.myapplication.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.ui.BikeDetailsViewModel
import com.example.myapplication.ui.BikeReportViewModel

class BikeDetailsViewModelFactory(
    private val bikeReportViewModel: BikeReportViewModel
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if the model class is BikeDetailsViewModel
        if (modelClass.isAssignableFrom(BikeDetailsViewModel::class.java)) {
            return BikeDetailsViewModel(bikeReportViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
