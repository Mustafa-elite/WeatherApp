package com.example.weatherforcast.model.data

import com.example.weatherforcast.R

data class CurrentWeatherResponse(
    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Long,
    val sys: Sys,
    val timezone: Int,
    val id: Int,
    val name: String,
    val cod: Int
)

data class Coord(
    val lon: Double,
    val lat: Double
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
) {
    val iconRes: Int
        get() = when (icon) {
            "01d" -> R.drawable._1d
            "01n" -> R.drawable._1n
            "02d" -> R.drawable._2d
            "02n" -> R.drawable._2n
            "03d", "03n" -> R.drawable._3d
            "04d", "04n" -> R.drawable._4d
            "09d", "09n" -> R.drawable._9d
            "10d" -> R.drawable._10d
            "10n"->R.drawable._10n
            "11d", "11n" -> R.drawable._11d
            "13d", "13n" -> R.drawable._13d
            "50d", "50n" -> R.drawable._50d
            else -> R.drawable.ic_humidity
        }
}

data class Main(
    var temp: Double,
    var feels_like: Double,
    var temp_min: Double,
    var temp_max: Double,
    val pressure: Int,
    val humidity: Int,
    val sea_level: Int,
    val grnd_level: Int
)

data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double
)

data class Clouds(
    val all: Int
)

data class Sys(
    val country: String,
    val sunrise: Long,
    val sunset: Long
)
