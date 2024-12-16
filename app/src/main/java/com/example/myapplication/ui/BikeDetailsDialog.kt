package com.example.myapplication.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.Bike
import com.example.myapplication.data.BikeDetailsViewModelFactory

@Composable
fun BikeDetailsDialog(
    bike: Bike,
    onDismiss: () -> Unit,
    bikeDetailsViewModel: BikeDetailsViewModel = viewModel(
        factory = BikeDetailsViewModelFactory(bikeReportViewModel = viewModel())
    ) // Passing BikeViewModel
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = bike.bikeName ?: "Unknown Bike") },
        text = {
            Text(
                text = bike.address ?: "No address available.",
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    bikeDetailsViewModel.updateBikeReturned(bike) // FireStore update via ViewModel
                    onDismiss() // Dismiss the dialog
                },
            ) {
                Text(text = "Return Bike")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(text = "Cancel")
            }
        }
    )
}
