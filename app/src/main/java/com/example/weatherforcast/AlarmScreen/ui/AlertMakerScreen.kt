package com.example.weatherforcast.AlarmScreen.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherforcast.AlarmScreen.AlertMakerViewModel
import com.example.weatherforcast.R
import com.example.weatherforcast.helpyclasses.AlertsManager
import com.example.weatherforcast.model.data.WeatherAlert
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun AlertMakerScreen(alertMakerViewModel: AlertMakerViewModel,afterAddingAction:()->Unit) {
    var selectedLocation by remember { mutableStateOf(LatLng(30.0444, 31.2357)) }
    var selectedDateTime by remember { mutableStateOf(System.currentTimeMillis() / 1000) }
    val context= LocalContext.current
    val markerState = rememberMarkerState(position = selectedLocation)

    Column(modifier = Modifier.fillMaxSize()) {

        Box(modifier = Modifier.weight(1f)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(selectedLocation, 10f)
                },
                onMapClick = { latLng ->
                    selectedLocation = latLng
                    markerState.position = latLng
                }
            ) {
                Marker(
                    state = markerState,
                    draggable = true
                )
            }
        }


        LaunchedEffect(markerState.position) {
            selectedLocation = markerState.position
        }


        DateAndTimePicker { selectedDateTime = it }


        Button(
            onClick = { if(alertMakerViewModel.checkTimeValidity(selectedDateTime))
            {
                val weatherAlert= WeatherAlert(
                lon = selectedLocation.longitude,
                lat = selectedLocation.latitude,
                alertDateTime = selectedDateTime)
                weatherAlert.updateLocation(context)
                alertMakerViewModel.addAlert(weatherAlert){
                    AlertsManager.scheduleWeatherAlert(context,it,selectedDateTime)
                }
                afterAddingAction()
            }else
            {
                Toast.makeText(context, context.getString(R.string.invalid_time),Toast.LENGTH_SHORT).show()
            }
                 },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(stringResource(R.string.confirm))
        }
    }
}

@Composable
fun DateAndTimePicker(onDateTimeSelected: (Long) -> Unit) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

    var selectedDate by remember { mutableStateOf(formatDate(calendar)) }
    var selectedTime by remember { mutableStateOf(formatTime(calendar)) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Date Picker
        Button(onClick = {
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    selectedDate = formatDate(calendar)
                    onDateTimeSelected(calendar.timeInMillis / 1000)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.pick_date, selectedDate))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Time Picker
        Button(onClick = {
            TimePickerDialog(
                context,
                { _, hour, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    selectedTime = formatTime(calendar)
                    onDateTimeSelected(calendar.timeInMillis / 1000)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.pick_time, selectedTime))
        }
    }
}

// Helper functions to format Date & Time
fun formatDate(calendar: Calendar): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(calendar.time)
}

fun formatTime(calendar: Calendar): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(calendar.time)
}
