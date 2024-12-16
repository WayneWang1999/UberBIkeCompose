package com.example.myapplication


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.myapplication.data.BikeViewModelFactory
import com.example.myapplication.ui.BikeReportScreen
import com.example.myapplication.ui.BikeReportViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.firebase.FirebaseApp


class MainActivity : ComponentActivity() {
    private val bikeReportViewModel: BikeReportViewModel by viewModels {
        BikeViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Call super before doing any UI-related work
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        if (!isLocationPermissionGranted()) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
        setContent {
            MyApplicationTheme {
                BikeReportScreen(
                    bikeReportViewModel = bikeReportViewModel,
                    context = this, // Pass the context
                )
            }
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}
