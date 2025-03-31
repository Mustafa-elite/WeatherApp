package com.example.weatherforcast.favouriteScreen

import com.example.weatherforcast.homeScreen.HomeViewResponse
import com.example.weatherforcast.model.data.WeatherInfo

sealed class FavViewResponse {
    data object Loading: FavViewResponse()
    data class Success(val dataList: List<WeatherInfo>): FavViewResponse()
    data class Failure(val data:Throwable): FavViewResponse()

}
