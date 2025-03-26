package com.example.weatherforcast.model.data

data class DailyWeatherResponse(
    val list: List<DailyWeatherData>
)

data class DailyWeatherData(
    val temp: ExtraTemp,
    val weather: List<Weather>
)

data class ExtraTemp(
    val min: Double,
    val max: Double
)