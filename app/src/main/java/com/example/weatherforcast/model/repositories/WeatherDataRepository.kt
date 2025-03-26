package com.example.weatherforcast.model.repositories

import android.util.Log
import com.example.weatherforcast.model.data.CurrentWeatherResponse
import com.example.weatherforcast.model.data.DailyWeatherResponse
import com.example.weatherforcast.model.data.WeatherInfo
import com.example.weatherforcast.model.remote.WeatherRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeatherDataRepository private constructor(private val remoteDataSource: WeatherRemoteDataSource) {


    companion object{
        private var dataRepository: WeatherDataRepository? =null
        fun getInstance(weatherRemoteDataSource: WeatherRemoteDataSource):WeatherDataRepository{
            return dataRepository ?: synchronized(this){
                val temp=WeatherDataRepository(weatherRemoteDataSource)
                dataRepository=temp
                return temp
            }
        }
    }
    suspend fun getRemoteWeatherLonLat(lon: Double, lat: Double): Flow<WeatherInfo> =flow {
        Log.i("TAG", "getRemoteWeatherLonLat:before getting data ")
        remoteDataSource.getCurrentWeatherLonLat(lon,lat)
            .combine(remoteDataSource.getDailyWeatherLonLat(lon,lat,1)){
                    current: CurrentWeatherResponse, daily: DailyWeatherResponse ->

                Log.i("TAG", "getRemoteWeatherLonLat: fetching data")
                WeatherInfo(
                    weatherDescription = current.weather[0].description,
                    windSpeed = current.wind.speed,
                    temp = current.main.temp,
                    feelsLike = current.main.feels_like,
                    minTemp = daily.list[0].temp.min,
                    maxTemp = daily.list[0].temp.max,
                    pressure = current.main.pressure,
                    humidityPercentage = current.main.humidity,
                    visibility = current.visibility,
                    cloudyPercentage = current.clouds.all,
                    calculationTime = current.dt,
                    timezone = current.timezone,
                    sunrise = current.sys.sunrise,
                    sunset = current.sys.sunset,
                    countryName = current.sys.country,
                    cityName = current.name,
                    iconInfo = current.weather[0].iconRes
                )
            }.flowOn(Dispatchers.IO)
                .collect { emit(it) }

    }



}