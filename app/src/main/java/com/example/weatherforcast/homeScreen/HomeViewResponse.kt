package com.example.weatherforcast.homeScreen

import com.example.weatherforcast.model.data.WeatherInfo

sealed class HomeViewResponse {
    data object Loading: HomeViewResponse()
    data class SuccessWeatherInfo(val data: WeatherInfo): HomeViewResponse()
    data class Failure(val data:Throwable): HomeViewResponse()

}