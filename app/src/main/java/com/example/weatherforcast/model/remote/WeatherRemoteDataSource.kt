package com.example.weatherforcast.model.remote

import android.util.Log
import com.example.weatherforcast.model.data.CurrentWeatherResponse
import com.example.weatherforcast.model.data.DailyWeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


class WeatherRemoteDataSource (private val weatherService: WeatherService){

    suspend fun getCurrentWeatherLonLat(lon:Double,lat:Double): Flow<CurrentWeatherResponse> {
        Log.i("TAG", "getCurrentWeatherLonLat: ")
        return flowOf(weatherService.getCurrentWeatherByLonLat(lon,lat))
    }
    suspend fun getDailyWeatherLonLat(lon:Double,lat:Double,numOfDays:Int=7): Flow<DailyWeatherResponse> {
        Log.i("TAG", "getDailyWeatherLonLat: ")
        return flowOf(weatherService.getDailyWeatherByLonLat(lon,lat,numOfDays))
    }
}