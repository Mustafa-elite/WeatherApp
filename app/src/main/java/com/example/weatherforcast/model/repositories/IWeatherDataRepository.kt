package com.example.weatherforcast.model.repositories

import com.example.weatherforcast.model.data.TemperatureUnit
import com.example.weatherforcast.model.data.WeatherAlert
import com.example.weatherforcast.model.data.WeatherInfo
import com.example.weatherforcast.model.data.WindSpeedUnit
import com.example.weatherforcast.model.local.WeatherLocalDatSource
import com.example.weatherforcast.model.remote.WeatherRemoteDataSource
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.flow.Flow

interface IWeatherDataRepository {
    suspend fun getWeatherInfo(
        lon: Double,
        lat: Double,
        isMainLocation: Boolean,
        isFavourite: Boolean
    ): Flow<WeatherInfo>

    suspend fun getFavData(): Flow<List<WeatherInfo>>
    fun getAutoCompleteText(
        query: String,
        placesClient: PlacesClient
    ): Task<FindAutocompletePredictionsResponse>

    fun fetchGoogleMapPlaceById(
        placeId: String,
        placesClient: PlacesClient
    ): Task<FetchPlaceResponse>

    fun getMainLocationId(): Int
    fun getWeatherInfoById(weatherId: Int): Flow<WeatherInfo>

    suspend fun removeFavWeatherById(weatherId: Int)

    suspend fun setMainWeather(weatherInfo: WeatherInfo)

    suspend fun getAlertsWeather(): Flow<List<WeatherAlert>>

    suspend fun addAlertWeather(weatherAlert: WeatherAlert): Long

    suspend fun removeAlertWeatherById(weatherAlertId: Int)

    suspend fun getAlertWeatherById(alertId: Int): WeatherAlert
    fun getLanguage(): String
    fun getTemperatureUnit(): TemperatureUnit
    fun getSpeedUnit(): WindSpeedUnit

    suspend fun setLanguage(lang: String)

    suspend fun setTemperatureUnit(unit: TemperatureUnit)

    suspend fun setSpeedUnit(unit: WindSpeedUnit)

}