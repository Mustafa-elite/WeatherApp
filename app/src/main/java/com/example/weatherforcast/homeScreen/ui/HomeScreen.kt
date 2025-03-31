package com.example.weatherforcast.homeScreen.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.core.content.ContextCompat
import com.example.weatherforcast.R
import com.example.weatherforcast.helpyclasses.LocationUtil
import com.example.weatherforcast.homeScreen.HomeViewModel
import com.example.weatherforcast.homeScreen.HomeViewResponse

@Composable
fun HomeScreen(homeViewModel: HomeViewModel){
    val context=LocalContext.current
    val weatherState by homeViewModel.weatherResponse.collectAsState()
    var location by remember { mutableStateOf<Location?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
                {throwable ->errorMessage=throwable.message})
        }
        else{
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

            locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION) )
        } else if(location==null){
            //location already granted but did not get location
            LocationUtil.getLonLatLocation(context,
                {loc->
                    location=loc
                    //homeViewModel.getRecentWeather(loc.longitude, loc.latitude)
                },
                {throwable ->errorMessage=throwable.message})
        }
    }
    LaunchedEffect(location) {
        location?.let { homeViewModel.getRecentWeather(it.longitude, it.latitude) }
    }


    Box (modifier = Modifier
        .fillMaxSize()
        .background(colorResource(R.color.test_color)),
        contentAlignment = Alignment.Center
    ){
        Column(modifier = Modifier
            .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally) {
            when(weatherState){
                is HomeViewResponse.Failure ->
                {
                    Toast.makeText(context,"response.failure in home screen",Toast.LENGTH_SHORT).show()
                }
                is HomeViewResponse.Loading -> {
                    WeatherDetailsScreen()
                }
                is HomeViewResponse.SuccessWeatherInfo -> WeatherDetailsScreen(weatherInfo = (weatherState as HomeViewResponse.SuccessWeatherInfo).data)
            }
        }


    }
}

