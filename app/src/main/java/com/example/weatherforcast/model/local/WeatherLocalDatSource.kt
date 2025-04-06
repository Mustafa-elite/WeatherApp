package com.example.weatherforcast.model.local

import com.example.weatherforcast.model.data.TemperatureUnit
import com.example.weatherforcast.model.data.WeatherAlert
import com.example.weatherforcast.model.data.WeatherInfo
import com.example.weatherforcast.model.data.WindSpeedUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext


class WeatherLocalDatSource (private val weatherDao: WeatherDao,
                             private val sharedPref:SharedPrefInterface
                             //private val sharedPreferences: SharedPreferences
) : IWeatherLocalDatSource {

    override suspend fun saveWeather(weatherInfo: WeatherInfo): Long {
        return withContext(Dispatchers.IO){
            val res=weatherDao.insertWeatherInfo(weatherInfo)
            //Log.i("TAG", "saveWeather: id:"+weatherInfo.weatherId+"returned:"+res)
            res
        }
    }
    override suspend fun updateWeather(weatherInfo: WeatherInfo){
        return withContext(Dispatchers.IO){
            weatherDao.updateWeatherInfo(weatherInfo)
        }
    }

    override suspend fun getWeatherById(weatherId:Int): WeatherInfo {
        return withContext(Dispatchers.IO){
            weatherDao.getWeatherById(weatherId)

        }
    }

    override suspend fun getAllStoredWeatherData(): Flow<List<WeatherInfo>>{
        return withContext(Dispatchers.IO){
            weatherDao.getAllWeathers()
        }

    }

    override suspend fun saveMainWeatherId(weatherId: Int) {
        return withContext(Dispatchers.IO) {
            sharedPref.addMainWeather(weatherId)

        }
    }

    override  fun getMainWeatherId(): Int {
        return sharedPref.getMainWeatherId()


    }

    override suspend fun removeFavWeatherById(weatherId: Int) {
        return withContext(Dispatchers.IO) {
            if(getMainWeatherId()==weatherId){
                sharedPref.removeMainWeather(weatherId)
            }
            weatherDao.removeWeatherById(weatherId)
        }
    }

    override suspend fun getAlertsWeather(): Flow<List<WeatherAlert>> {
        return withContext(Dispatchers.IO){
            weatherDao.getAlertWeathers()
        }

    }

    override suspend fun addAlertWeather(weatherAlert: WeatherAlert) :Long{
        return withContext(Dispatchers.IO){
            weatherDao.addAlertWeather(weatherAlert)
        }
    }

    override suspend fun removeAlertWeatherById(weatherAlertId: Int) {
        return withContext(Dispatchers.IO){
            weatherDao.removeWeatherAlertById(weatherAlertId)
        }

    }

    override suspend fun getWeatherAlertById(alertId: Int):WeatherAlert {
        return withContext(Dispatchers.IO){
            weatherDao.getAlertWeatherById(alertId)
        }

    }

    override fun getLanguage(): String {
        return sharedPref.getLanguage()
    }

    override fun getTemperatureUnit(): TemperatureUnit {
        return TemperatureUnit.valueOf(sharedPref.getTemperatureUnit())
    }

    override fun getSpeedUnit(): WindSpeedUnit {
        return WindSpeedUnit.valueOf(sharedPref.getSpeedUnit())
    }

    override suspend fun setLanguage(lang: String) {
        withContext(Dispatchers.IO){
            sharedPref.setLanguage(lang)
        }
    }

    override suspend fun setTemperatureUnit(unit: TemperatureUnit) {
        withContext(Dispatchers.IO){
            sharedPref.setTemperatureUnit(unit)
        }

    }

    override suspend fun setSpeedUnit(unit: WindSpeedUnit) {
        withContext(Dispatchers.IO){
            sharedPref.setSpeedUnit(unit)
        }

    }

}