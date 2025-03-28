package com.example.weatherforcast.model.repositories

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.weatherforcast.model.data.CurrentWeatherResponse
import com.example.weatherforcast.model.data.DailyWeatherResponse
import com.example.weatherforcast.model.data.WeatherInfo
import com.example.weatherforcast.model.remote.WeatherRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.time.LocalDate

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

        combine(
            remoteDataSource.getCurrentWeatherLonLat(lon,lat),
            remoteDataSource.getDailyWeatherLonLat(lon,lat,7) ,
            remoteDataSource.getThreeHoursForecastLonLat(lon,lat,7)
        ){
            currentWeather,dailyWeather,threeHoursWeather->


            Log.i("TAG", "getRemoteWeatherLonLat: fetching data" )
            WeatherInfo(
                weatherDescription = currentWeather.weather[0].description,
                windSpeed = currentWeather.wind.speed,
                temp = currentWeather.main.temp,
                feelsLike = currentWeather.main.feels_like,
                minTemp = dailyWeather.list[0].temp.min,
                maxTemp = dailyWeather.list[0].temp.max,
                pressure = currentWeather.main.pressure,
                humidityPercentage = currentWeather.main.humidity,
                visibility = currentWeather.visibility,
                cloudyPercentage = currentWeather.clouds.all,
                calculationTime = currentWeather.dt,
                timezone = currentWeather.timezone,
                sunrise = currentWeather.sys.sunrise,
                sunset = currentWeather.sys.sunset,
                countryName = currentWeather.sys.country,
                cityName = currentWeather.name,
                daysForecast = dailyWeather.list,
                threeHoursForecast = threeHoursWeather.list,
                iconInfo = currentWeather.weather[0].iconRes
            )
        }.flowOn(Dispatchers.IO)
            .collect { emit(it) }

    }



}