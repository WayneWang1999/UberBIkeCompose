package com.example.myapplication

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.Bike
import com.example.myapplication.util.BikeDataInputs
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


@Composable
fun BikeReportScreen(
    bikeViewModel: BikeViewModel,
    context: Context, // Pass the context
    onGenerateRandomBike: () -> Unit
) {
    var isFilterApplied by remember { mutableStateOf(false) }
    var selectedBike by remember { mutableStateOf<Bike?>(null) }

    val bikes by bikeViewModel.bikeLocations.observeAsState(initial = emptyList())

    // Handle bike returned event
    val onBikeReturned: (Bike) -> Unit = { updatedBike ->
        // Pass the updated bike to the ViewModel or database
        bikeViewModel.updateBikeReturnStatus(updatedBike)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp) // Add shadow for elevation effect
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Bike Report",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Find",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = isFilterApplied,
                        onCheckedChange = {
                            isFilterApplied = it
                            bikeViewModel.fetchBikeLocations(it)
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Home",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Map Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            val toronto = LatLng(43.6532, -79.3832)
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(toronto, 10f)
            }
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                // Add markers for bike locations
                bikes.forEach { bike ->
                    Marker(
                        state = MarkerState(LatLng(bike.latitude, bike.longitude)),
                        title = bike.bikeName,
                        snippet = bike.address,
                        onClick = {
                            selectedBike = bike
                            true // Return true to indicate that the click was handled
                        }
                    )
                }
            }
        }

        // Handle Bike Details Dialog
        selectedBike?.let { bike ->
            BikeDetailsDialog(
                bike = bike,
                onDismiss = { selectedBike = null },
                onBikeReturned = onBikeReturned // Pass the function to the dialog
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Generate Random Bike Button
        Button(
            onClick = {
                // Call the function to generate and upload a random bike
                val bikeDataInputs = BikeDataInputs(context)
                bikeDataInputs.generateRandomBikeAndUpload()
                // After inserting the bike, refresh the bike list in the ViewModel
                bikeViewModel.fetchBikeLocations(false)
            },
        ) {
            Text(text = "Generate Random Bike")
        }
    }
}
