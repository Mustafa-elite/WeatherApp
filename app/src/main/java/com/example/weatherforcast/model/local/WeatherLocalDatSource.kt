package com.example.weatherforcast.model.local

import com.example.weatherforcast.model.data.TemperatureUnit
import com.example.weatherforcast.model.data.WeatherInfo
import com.example.weatherforcast.model.data.WindSpeedUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class WeatherLocalDatSource (private val weatherDao: WeatherDao){

    suspend fun saveWeather(weatherInfo: WeatherInfo): Long {
        return withContext(Dispatchers.IO){
            weatherDao.insertWeatherInfo(weatherInfo)
        }
    }
    suspend fun updateWeather(weatherInfo: WeatherInfo){
        return withContext(Dispatchers.IO){
            weatherDao.updateWeatherInfo(weatherInfo)
        }
    }

    suspend fun getWeatherById(weatherId:Int): WeatherInfo {
        return withContext(Dispatchers.IO){
            weatherDao.getWeatherById(weatherId)
        }
    }

    suspend fun getAllStoredWeatherData(): Flow<List<WeatherInfo>>{
        return withContext(Dispatchers.IO){
            weatherDao.getAllWeathers()
        }

    }
}