package com.example.weatherforcast.model.local

import com.example.weatherforcast.model.data.WeatherInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class WeatherLocalDatSource (private val weatherDao: WeatherDao,
                            private val sharedPref:SharedPrefInterface
                             //private val sharedPreferences: SharedPreferences
){

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

    suspend fun saveMainWeatherId(weatherId: Int) {
        return withContext(Dispatchers.IO) {
            sharedPref.addMainWeather(weatherId)

        }
    }

    fun getMainWeatherId(): Int {
        return sharedPref.getMainWeatherId()


    }

    suspend fun removeFavWeatherById(weatherId: Int) {
        return withContext(Dispatchers.IO) {
            if(getMainWeatherId()==weatherId){
                sharedPref.removeMainWeather(weatherId)
            }
            weatherDao.removeWeatherById(weatherId)
        }
    }

}