package com.example.weatherforcast.model.data

data class ThreeHoursForecastResponse(
    val cnt: Int,
    val list: MutableList<ThreeHoursWeatherData>,
)
data class ThreeHoursWeatherData(
    val dt:Long,
    val main: Main,
    val weather: List<Weather>,
)