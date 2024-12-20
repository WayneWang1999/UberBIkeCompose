package com.example.myapplication.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.BikeDetailsViewModelFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


@Composable
fun BikeReportScreen(
    bikeReportViewModel: BikeReportViewModel,
    context: Context, // Pass the context
) {
// *********Use StateFlow to define state variables
    val bikes by bikeReportViewModel.bikeLocations.collectAsState()
    val isFilterApplied by bikeReportViewModel.isFilterApplied.collectAsState()
    val selectedBike by bikeReportViewModel.selectedBike.collectAsState()



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp) // Add shadow for elevation effect
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 12.dp,
                        end = 12.dp,
                        top = 2.dp,
                        bottom = 2.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Bike Report",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(8.dp) // Rounded corners
                        )
                        .padding(vertical = 8.dp, horizontal = 16.dp) // Padding inside the button
                        .clickable {
                            // Call the function to generate and upload a random bike
                            bikeReportViewModel.generateAndUploadBike(context)
                        }
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Find",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Switch(
                        checked = isFilterApplied,
                        onCheckedChange = {
                            bikeReportViewModel.setFilterApplied(it)
                        }
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "Home",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 20.sp
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
                            bikeReportViewModel.setSelectedBike(bike)
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
                onDismiss = { bikeReportViewModel.setSelectedBike(null) },
                bikeDetailsViewModel = viewModel(
                    factory = BikeDetailsViewModelFactory(bikeReportViewModel)
                )
            )
        }
    }
}
