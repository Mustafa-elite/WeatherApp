package com.example.weatherforcast.model.repositories

import android.util.Log
import com.example.weatherforcast.helpyclasses.DateManager
import com.example.weatherforcast.model.data.TemperatureUnit
import com.example.weatherforcast.model.data.WeatherInfo
import com.example.weatherforcast.model.data.WindSpeedUnit
import com.example.weatherforcast.model.local.WeatherLocalDatSource
import com.example.weatherforcast.model.remote.WeatherRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class WeatherDataRepository private constructor(
    private val remoteDataSource: WeatherRemoteDataSource,
    private val localDataSource: WeatherLocalDatSource) {


    companion object{
        private var dataRepository: WeatherDataRepository? =null
        fun getInstance(
            weatherRemoteDataSource: WeatherRemoteDataSource,
            localDataSource: WeatherLocalDatSource
        ):WeatherDataRepository{
            return dataRepository ?: synchronized(this){
                val temp=WeatherDataRepository(weatherRemoteDataSource,
                    localDataSource)
                dataRepository=temp
                return temp
            }
        }
    }

    suspend fun getWeatherInfo(lon: Double, lat: Double): Flow<WeatherInfo> = flow {
        val weatherList=localDataSource.getAllStoredWeatherData().first()
            for(storedWeather in weatherList){
                if (isNearbyLocation(storedWeather.lon,storedWeather.lat,lon,lat,5.0)){
                    val validatedWeather=validateWeatherData(storedWeather)
                    //get from sharedPref
                    validatedWeather.convertWindSpeedUnit(WindSpeedUnit.KILOMETER_HOUR)
                    validatedWeather.convertTemperatureUnit(TemperatureUnit.CELSIUS)
                    emit(validatedWeather)
                    localDataSource.updateWeather(validatedWeather)
                    return@flow
                }
            }
        val remoteWeatherDetails=remoteDataSource.getWeatherDetails(lon,lat)

        //get from sharedPref
        remoteWeatherDetails.convertWindSpeedUnit(WindSpeedUnit.KILOMETER_HOUR)
        remoteWeatherDetails.convertTemperatureUnit(TemperatureUnit.CELSIUS)
        emit(remoteWeatherDetails)
        localDataSource.saveWeather(remoteWeatherDetails)
    }.flowOn(Dispatchers.IO)

    private suspend fun validateWeatherData(weatherInfo: WeatherInfo): WeatherInfo {

        weatherInfo.convertWindSpeedUnit(WindSpeedUnit.METER_SECOND)
        weatherInfo.convertTemperatureUnit(TemperatureUnit.KELVIN)
        val validatedCurrent=validateCurrentData(weatherInfo)
        val validatedThreeHours=validateThreeHoursForecast(validatedCurrent)
        val validatedDaily = validateDailyForecast(validatedThreeHours)
        return validatedDaily
    }

    private suspend  fun validateDailyForecast(weatherInfo: WeatherInfo): WeatherInfo {
        weatherInfo.daysForecast.removeIf{
            DateManager.hasTimePassed(it.dt+54000)//it.dt=time at 9:00 am,so add 15 hour*3600 to make it at the end of the day
        }
        if(weatherInfo.daysForecast.size<7){
            val response=remoteDataSource.getDailyWeatherLonLat(weatherInfo.lon,weatherInfo.lat).first()
            weatherInfo.fetchDailyWeatherData(response)
            return weatherInfo
        }
        else{
            return weatherInfo
        }
    }

    private suspend fun validateThreeHoursForecast(weatherInfo: WeatherInfo): WeatherInfo {
        weatherInfo.threeHoursForecast.removeIf{
            DateManager.hasTimePassed(it.dt)
        }
        if(weatherInfo.threeHoursForecast.size<8){
            val response=remoteDataSource.getThreeHoursForecastLonLat(weatherInfo.lon,weatherInfo.lat).first()
            weatherInfo.fetchThreeHoursWeatherData(response)
            return weatherInfo
        }
        else{
            return weatherInfo
        }
    }

    private suspend fun validateCurrentData(weatherInfo: WeatherInfo): WeatherInfo {
        if(DateManager.hasDurationPassed(weatherInfo.calculationTime, hours = 1)){
            val response=remoteDataSource.getCurrentWeatherLonLat(weatherInfo.lon,weatherInfo.lat).first()
            weatherInfo.fetchCurrentWeatherData(response)
            return weatherInfo
        }
        else{
            return weatherInfo
        }
    }

    /*suspend fun getRemoteWeatherLonLat(lon: Double, lat: Double): Flow<WeatherInfo> =flow {
        Log.i("TAG", "getRemoteWeatherLonLat:before getting data ")

        combine(
            remoteDataSource.getCurrentWeatherLonLat(lon,lat),
            remoteDataSource.getDailyWeatherLonLat(lon,lat,9) ,
            remoteDataSource.getThreeHoursForecastLonLat(lon,lat,12)
        ){
            currentWeather,dailyWeather,threeHoursWeather->


            Log.i("TAG", "getRemoteWeatherLonLat: fetching data" )
            WeatherInfo(
                lon=currentWeather.coord.lon,
                lat = currentWeather.coord.lat,
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

    }*/


    private fun isNearbyLocation(lon1: Double,lat1: Double ,lon2: Double, lat2: Double ,requiredDistanceKm:Double): Boolean {
        val earthRadius = 6371
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distance = earthRadius * c
        return distance < requiredDistanceKm
    }



}