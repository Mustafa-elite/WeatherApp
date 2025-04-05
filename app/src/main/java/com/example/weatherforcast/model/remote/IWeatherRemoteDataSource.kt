package com.example.weatherforcast.model.remote

import com.example.weatherforcast.model.data.CurrentWeatherResponse
import com.example.weatherforcast.model.data.DailyWeatherResponse
import com.example.weatherforcast.model.data.ThreeHoursForecastResponse
import com.example.weatherforcast.model.data.WeatherInfo
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.flow.Flow

interface IWeatherRemoteDataSource {
    suspend fun getCurrentWeatherLonLat(lon: Double, lat: Double): Flow<CurrentWeatherResponse>

    suspend fun getDailyWeatherLonLat(
        lon: Double,
        lat: Double,
        numOfDays: Int = 7
    ): Flow<DailyWeatherResponse>

    suspend fun getThreeHoursForecastLonLat(
        lon: Double,
        lat: Double,
        numOfForecasts: Int = 9
    ): Flow<ThreeHoursForecastResponse>

    suspend fun getWeatherDetails(lon: Double, lat: Double): WeatherInfo
    fun getPlacesApiAutoComplete(
        query: String,
        placesClient: PlacesClient
    ): Task<FindAutocompletePredictionsResponse>

    fun fetchPlaceById(placeId: String, placesClient: PlacesClient): Task<FetchPlaceResponse>
}