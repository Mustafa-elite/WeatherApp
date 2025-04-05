package com.example.weatherforcast.model.local

import com.example.weatherforcast.model.data.TemperatureUnit
import com.example.weatherforcast.model.data.WindSpeedUnit

interface SharedPrefInterface {
    fun addMainWeather(weatherId: Int)
    fun getMainWeatherId(): Int
    fun removeMainWeather(weatherId: Int)

    fun getLanguage(): String
    fun setLanguage(language: String)
    fun getTemperatureUnit(): String
    fun setTemperatureUnit(unit: TemperatureUnit)
    fun getSpeedUnit(): String
    fun setSpeedUnit(unit: WindSpeedUnit)
}
