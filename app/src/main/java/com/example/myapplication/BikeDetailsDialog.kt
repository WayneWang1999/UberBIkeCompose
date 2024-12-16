package com.example.myapplication

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.myapplication.data.Bike

@Composable
fun BikeDetailsDialog(bike: Bike, onDismiss: () -> Unit, onBikeReturned: (Bike) -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = bike.bikeName ?: "Unknown Bike") },
        text = { Text(text = bike.address ?: "No address available.") },
        confirmButton = {
            TextButton(onClick = {
                // Update the bike's 'returned' status and trigger the callback
                val updatedBike = bike.copy(isReturned = true) // Assuming 'returned' is a Boolean field in the Bike object
                onBikeReturned(updatedBike) // Update the bike state via the callback
                onDismiss() // Close the dialog
            }) {
                Text(text = "Return Bike")
            }
        }
    )
}