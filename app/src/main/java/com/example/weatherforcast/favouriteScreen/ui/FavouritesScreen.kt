package com.example.weatherforcast.favouriteScreen.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherforcast.R
import com.example.weatherforcast.favouriteScreen.FavViewResponse
import com.example.weatherforcast.favouriteScreen.FavouritesViewModel
import com.example.weatherforcast.model.data.WeatherInfo

@Composable
fun FavouritesScreen(
    favViewModel: FavouritesViewModel,
    navigateToMapAction:()->Unit,
    favItemClick: (WeatherInfo) -> Unit){
    val context=LocalContext.current
    val favWeatherResponse by favViewModel.favWeatherResponse.collectAsState()

    LaunchedEffect(Unit) {
        favViewModel.getFavData()
    }
    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        FloatingActionButton(
            onClick = { navigateToMapAction() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(R.string.add_to_favorite))
        }

        when(favWeatherResponse){
            is FavViewResponse.Failure -> TODO()
            is FavViewResponse.Loading -> LoadingIndicator()
            is FavViewResponse.Success -> {
                val weatherList=(favWeatherResponse as FavViewResponse.Success).dataList
                FavList(
                    weatherList,
                    {weatherInfo->favViewModel.isMainWeather(weatherInfo)},
                    {weatherInfo->favViewModel.removeFavItem(weatherInfo){
                        if(it){
                             context.getString(R.string.deleted_successfully)
                        }else
                        {
                            context.getString(R.string.internal_problem)
                        }
                    } },
                    {weatherInfo->favViewModel.makeDefaultWeather(weatherInfo)},
                    favItemClick)
            }
        }
    }
}


