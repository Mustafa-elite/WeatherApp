package com.example.weatherforcast.model.repositories

import com.example.weatherforcast.helpyclasses.DateManager
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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

    suspend fun getWeatherInfo(lon: Double, lat: Double,isMainLocation:Boolean,isFavourite:Boolean ): Flow<WeatherInfo> = flow {
        val weatherList=localDataSource.getAllStoredWeatherData().first()
            for(storedWeather in weatherList){
                if (isNearbyLocation(storedWeather.lon,storedWeather.lat,lon,lat,5.0)){
                    val validatedWeather=validateWeatherData(storedWeather)
                    if(isMainLocation){
                        localDataSource.saveMainWeatherId(validatedWeather.weatherId)

                    }
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
        if(isFavourite){
            val weatherId=localDataSource.saveWeather(remoteWeatherDetails)
            if(isMainLocation){
                localDataSource.saveMainWeatherId(weatherId.toInt())
            }
        }

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

    suspend fun getFavData(): Flow<List<WeatherInfo>> = flow {
        localDataSource.getAllStoredWeatherData().collect { weatherList ->
            val updatedList = weatherList.map { storedWeather ->
                val validatedWeather = validateWeatherData(storedWeather).apply {
                    convertWindSpeedUnit(WindSpeedUnit.KILOMETER_HOUR)
                    convertTemperatureUnit(TemperatureUnit.CELSIUS)
                }

                if (validatedWeather != storedWeather) {
                    localDataSource.updateWeather(validatedWeather)
                }
                validatedWeather
            }
            emit(updatedList)
        }
    }.flowOn(Dispatchers.IO)



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

    fun getAutoCompleteText(query: String, placesClient: PlacesClient): Task<FindAutocompletePredictionsResponse> {
        return remoteDataSource.getPlacesApiAutoComplete(query,placesClient)
    }

    fun fetchGoogleMapPlaceById(placeId: String, placesClient: PlacesClient): Task<FetchPlaceResponse> {
        return remoteDataSource.fetchPlaceById(placeId,placesClient)

    }

     fun getMainLocationId(): Int {
        return localDataSource.getMainWeatherId()
    }

     fun getWeatherInfoById(weatherId: Int):Flow<WeatherInfo> = flow  {
         val weatherInfo=validateWeatherData(localDataSource.getWeatherById(weatherId))
         //get from shared pref
         weatherInfo.convertWindSpeedUnit(WindSpeedUnit.KILOMETER_HOUR)
         weatherInfo.convertTemperatureUnit(TemperatureUnit.CELSIUS)
         emit(weatherInfo)
    }.flowOn(Dispatchers.IO)

    suspend fun removeFavWeatherById(weatherId: Int) {
        return localDataSource.removeFavWeatherById(weatherId)
    }

    suspend fun setMainWeather(weatherInfo: WeatherInfo) {
        localDataSource.saveMainWeatherId(weatherInfo.weatherId)

    }

    suspend fun getAlertsWeather() : Flow<List<WeatherAlert>> {
        return localDataSource.getAlertsWeather()
    }

    suspend fun addAlertWeather(weatherAlert: WeatherAlert): Long {
        return localDataSource.addAlertWeather(weatherAlert)
    }


    suspend fun removeAlertWeatherById(weatherAlertId: Int) {
        localDataSource.removeAlertWeatherById(weatherAlertId)
    }

    suspend fun getAlertWeatherById(alertId: Int): WeatherAlert {
        return localDataSource.getWeatherAlertById(alertId)
    }
}