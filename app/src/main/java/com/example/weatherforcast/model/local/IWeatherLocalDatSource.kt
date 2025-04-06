package com.example.weatherforcast.model.local

import com.example.weatherforcast.model.data.TemperatureUnit
import com.example.weatherforcast.model.data.WeatherAlert
import com.example.weatherforcast.model.data.WeatherInfo
import com.example.weatherforcast.model.data.WindSpeedUnit
import kotlinx.coroutines.flow.Flow

interface IWeatherLocalDatSource {
    suspend fun saveWeather(weatherInfo: WeatherInfo): Long

    suspend fun updateWeather(weatherInfo: WeatherInfo)
    suspend fun getWeatherById(weatherId: Int): WeatherInfo

    suspend fun getAllStoredWeatherData(): Flow<List<WeatherInfo>>
    suspend fun saveMainWeatherId(weatherId: Int)
    fun getMainWeatherId(): Int

    suspend fun removeFavWeatherById(weatherId: Int)

    suspend fun getAlertsWeather(): Flow<List<WeatherAlert>>

    suspend fun addAlertWeather(weatherAlert: WeatherAlert) : Long
    suspend fun removeAlertWeatherById(weatherAlertId: Int)

    suspend fun getWeatherAlertById(alertId: Int): WeatherAlert
    fun getLanguage(): String
    fun getTemperatureUnit(): TemperatureUnit
    fun getSpeedUnit(): WindSpeedUnit

    suspend fun setLanguage(lang: String)

    suspend fun setTemperatureUnit(unit: TemperatureUnit)

    suspend fun setSpeedUnit(unit: WindSpeedUnit)
    suspend fun updateWeatherAlert(weatherAlert: WeatherAlert)
}