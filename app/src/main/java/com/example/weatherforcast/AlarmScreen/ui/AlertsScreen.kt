package com.example.weatherforcast.AlarmScreen.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherforcast.AlarmScreen.AlertViewModel
import com.example.weatherforcast.AlarmScreen.AlertViewResponse
import com.example.weatherforcast.R
import com.example.weatherforcast.favouriteScreen.ui.LoadingIndicator
import com.example.weatherforcast.favouriteScreen.ui.WeatherItem
import com.example.weatherforcast.helpyclasses.AlertsManager
import com.example.weatherforcast.helpyclasses.DateManager
import com.example.weatherforcast.model.data.WeatherAlert
import com.example.weatherforcast.model.data.WeatherInfo
import com.google.maps.android.compose.GoogleMap

@Composable
fun AlertsScreen(
    alertViewModel: AlertViewModel,
    navigateToMapAction: () -> Unit
) {
    val context = LocalContext.current
    val alertResponse by alertViewModel.alertViewResponse.collectAsState()
    LaunchedEffect(Unit) {
        alertViewModel.getAlerts()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        FloatingActionButton(
            onClick = { navigateToMapAction() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(R.string.add_to_favorite))
        }

        when (alertResponse) {
            is AlertViewResponse.Failure -> TODO()
            AlertViewResponse.Loading -> LoadingIndicator()
            is AlertViewResponse.Success -> {
                val alertList = (alertResponse as AlertViewResponse.Success).dataList
                AlertListScreen(alertList,
                    { weatherAlert ->
                        alertViewModel.deleteAlert(weatherAlert) {
                            AlertsManager.cancelWeatherAlert(context, it)
                        }
                    })
            }

        }


    }

}

@Composable
fun AlertListScreen(
    weatherAlertList: List<WeatherAlert>,
    alertItemOnDelete: (WeatherAlert) -> Unit
) {
    LazyColumn {
        items(weatherAlertList) {
            AlertItem(it, alertItemOnDelete)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertItem(
    weatherAlert: WeatherAlert,
    alertItemOnDelete: (WeatherAlert) -> Unit
) {
    weatherAlert.updateLocation(LocalContext.current)
    val formattedDate = remember(weatherAlert.alertDateTime) {
        DateManager.getLocalDateTimeFromSeconds(weatherAlert.alertDateTime)
    }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart || dismissValue == SwipeToDismissBoxValue.StartToEnd) {
                alertItemOnDelete(weatherAlert)
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = Color.Red
                )
            }
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(16.dp)),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${weatherAlert.cityName}, ${weatherAlert.countryName}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = stringResource(R.string.lat_lon, weatherAlert.lat, weatherAlert.lon),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = stringResource(R.string.time, formattedDate),
                        fontSize = 14.sp,
                        color = Color.Blue
                    )
                }
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = stringResource(R.string.alarm_image),
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

