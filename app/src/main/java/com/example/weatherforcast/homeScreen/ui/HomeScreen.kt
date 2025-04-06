package com.example.weatherforcast.homeScreen.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.weatherforcast.R
import com.example.weatherforcast.helpyclasses.LocationUtil
import com.example.weatherforcast.homeScreen.HomeViewModel
import com.example.weatherforcast.homeScreen.HomeViewResponse
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    snackBarHostState: SnackbarHostState,
    onLocationDeniedAction: () -> Unit,
    onChangeLocationRequested: () -> Unit
){
    val context=LocalContext.current
    val weatherState by homeViewModel.weatherResponse.collectAsState()
    var location by remember { mutableStateOf<Location?>(null) }

    val errorMessageRes by homeViewModel.viewMessage.collectAsState(R.string.Emtpy_String)
    var messageStr by  remember { mutableStateOf("") }

    val locationPermissionLauncher= rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()){permissions->
        val isGranted=permissions[Manifest.permission.ACCESS_FINE_LOCATION]==true||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (isGranted) {
            LocationUtil.getLonLatLocation(context,
                {loc->
                    location=loc
                    //homeViewModel.getRecentWeather(loc.longitude, loc.latitude)
                },
                {throwable ->messageStr=(throwable.message ?: "No Internet or Internal Server Error")})
        }
        else{
            onLocationDeniedAction.invoke()
            //handle location denied(navigate to mapscreen to choose location manually)

        }
    }
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ){
            delay(1000)
            if(homeViewModel.hasDefaultLocation()){
                Log.i("TAG", "HomeScreen: no location permission but has default location")
                homeViewModel.getDefaultLocation()
            }
            else{
                locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION) )
            }

        } else if(location==null){

            //location already granted but did not get location
            if(homeViewModel.hasDefaultLocation()){
                Log.i("TAG", "HomeScreen: has location permission but has default location")
                homeViewModel.getDefaultLocation()
            }
            else{
                LocationUtil.getLonLatLocation(context,
                    {loc->
                        location=loc
                        //homeViewModel.getRecentWeather(loc.longitude, loc.latitude)
                    },
                    {throwable -> messageStr=(throwable.message ?:"No Internet or Internal Server Error" ) })
            }

        }
    }
    LaunchedEffect(location) {
        location?.let { homeViewModel.getRecentWeather(it.longitude, it.latitude) }
    }


    LaunchedEffect(errorMessageRes) {
        Log.i("TAG", "HomeScreen: $errorMessageRes")
        messageStr=context.getString(errorMessageRes)
    }
    LaunchedEffect (messageStr){
        if (messageStr.isNotBlank()) {
            snackBarHostState.showSnackbar(messageStr)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.test_color)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (weatherState) {
                is HomeViewResponse.Failure -> {
                    WeatherDetailsScreen()
                }
                is HomeViewResponse.Loading -> {
                    WeatherDetailsScreen()
                }
                is HomeViewResponse.SuccessWeatherInfo -> WeatherDetailsScreen(
                    weatherInfo = (weatherState as HomeViewResponse.SuccessWeatherInfo).data,
                    navigateToHome = onChangeLocationRequested,
                    navigateToMap = onLocationDeniedAction
                )
            }
        }
    }
}

