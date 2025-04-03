package com.example.weatherforcast.AlarmScreen

import com.example.weatherforcast.model.data.WeatherAlert
import com.example.weatherforcast.model.data.WeatherInfo

sealed class AlertViewResponse {
    data object Loading: AlertViewResponse()
    data class Success(val dataList: List<WeatherAlert>): AlertViewResponse()
    data class Failure(val data:Throwable): AlertViewResponse()

}