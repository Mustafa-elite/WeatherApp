package com.example.weatherforcast.model.data

data class DailyWeatherResponse(
    val list: MutableList<DailyWeatherData>
)

data class DailyWeatherData(
    var dt: Long,
    val temp: ExtraTemp,
    val weather: List<Weather>
)

data class ExtraTemp(
    var min: Double,
    var max: Double
)