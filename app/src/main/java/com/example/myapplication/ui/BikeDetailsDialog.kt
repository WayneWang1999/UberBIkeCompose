package com.example.myapplication.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.myapplication.data.Bike

@Composable
fun BikeDetailsDialog(
    bike: Bike,
    onDismiss: () -> Unit,
    bikeDetailsViewModel: BikeDetailsViewModel
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
