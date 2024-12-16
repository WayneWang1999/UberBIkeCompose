package com.example.myapplication.data

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.ui.BikeViewModel

class BikeViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BikeViewModel::class.java)) {
            return BikeViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
