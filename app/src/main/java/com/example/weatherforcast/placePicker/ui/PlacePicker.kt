package com.example.weatherforcast.placePicker.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherforcast.placePicker.PlacesViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun PlacePicker(placesViewModel: PlacesViewModel,onConfirm:(LatLng) -> Unit) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(30.0444, 31.2357), 10f)
    }
    val selectedLocation by  placesViewModel.addressLatLon.collectAsState()

    val addressName by placesViewModel.addressName.collectAsState()


    Column (modifier = Modifier.fillMaxSize()){
        SearchPlaceBar(
            placesViewModel,
            onQueryChange = {placesViewModel.changeQuery(it)},
            onPlaceSelected = {address ->
                placesViewModel.fetchPlaceById(address) {latlon->
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(latlon, 15f)
                }

            }
        )
        GoogleMap(
            modifier = Modifier.weight(1f),
            cameraPositionState = cameraPositionState,
            onMapClick = {latLng ->  placesViewModel.updateLocation(latLng) }){
            selectedLocation?.let {
                Marker(
                    state = MarkerState(position =it),
                    title = addressName ?: "selected Location",
                    draggable = true,
                    onClick = {false}
                )
            }

        }
        Button(
            onClick = { selectedLocation?.let { latLng -> onConfirm(latLng)} },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text("Confirm Location")
        }
    }

}

@Composable
fun MapPicker(viewModel: PlacesViewModel) {

}

@Composable
fun SearchPlaceBar(viewModel: PlacesViewModel,
    onQueryChange: (String) -> Unit,
    onPlaceSelected: /*(LatLng, */(String) -> Unit
) {
    var query by  remember { mutableStateOf("") }
    val predictions by viewModel.predictions.collectAsState()

    Column (modifier = Modifier
        .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){

        OutlinedTextField(
            value=query,
            onValueChange = {
                query=it
                onQueryChange(it) },
            label = {Text("Search Location")},
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp))

        LazyColumn {
            items(predictions){prediction->
                Text(text = prediction.getPrimaryText(null).toString(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onPlaceSelected(prediction.placeId)
                        }
                        .padding(16.dp)
                )
            }
        }

    }

}
