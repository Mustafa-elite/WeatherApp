package com.example.weatherforcast.model.local

import com.example.weatherforcast.model.data.TemperatureUnit
import com.example.weatherforcast.model.data.WeatherAlert
import com.example.weatherforcast.model.data.WeatherInfo
import com.example.weatherforcast.model.data.WindSpeedUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeLocalDataSource(
    private val weatherList: MutableList<WeatherInfo> = mutableListOf()
) : IWeatherLocalDatSource {

    override suspend fun saveWeather(weatherInfo: WeatherInfo): Long {
        val newWeather = WeatherInfo(weatherId = weatherList.size + 1)
        weatherList.add(newWeather)
        return newWeather.weatherId.toLong()
    }

    override suspend fun updateWeather(weatherInfo: WeatherInfo) {
        val index = weatherList.indexOfFirst { it.weatherId == weatherInfo.weatherId }
        if (index != -1) {
            weatherList[index] = weatherInfo
        }
    }

    override suspend fun getWeatherById(weatherId: Int): WeatherInfo {
        return weatherList.firstOrNull { it.weatherId == weatherId }
            ?: throw NoSuchElementException("WeatherInfo with ID $weatherId not found")
    }

    override suspend fun getAllStoredWeatherData(): Flow<List<WeatherInfo>> {
        return flow { emit(weatherList) }
    }

    override suspend fun saveMainWeatherId(weatherId: Int) {
        TODO("Not yet implemented")
    }

    override fun getMainWeatherId(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun removeFavWeatherById(weatherId: Int) {
        weatherList.removeAll { it.weatherId == weatherId }
    }

    override suspend fun getAlertsWeather(): Flow<List<WeatherAlert>> {
        return flow { emit(emptyList()) }
    }

    override suspend fun addAlertWeather(weatherAlert: WeatherAlert): Long {
        TODO("Not yet implemented")
    }

    override suspend fun removeAlertWeatherById(weatherAlertId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun getWeatherAlertById(alertId: Int): WeatherAlert {
        TODO("Not yet implemented")
    }

    override fun getLanguage(): String {
        TODO("Not yet implemented")
    }

    override fun getTemperatureUnit(): TemperatureUnit {
        return TemperatureUnit.KELVIN
    }

    override fun getSpeedUnit(): WindSpeedUnit {
        return WindSpeedUnit.METER_SECOND
    }

    override suspend fun setLanguage(lang: String) {
        TODO("Not yet implemented")
    }

    override suspend fun setTemperatureUnit(unit: TemperatureUnit) {
        TODO("Not yet implemented")
    }

    override suspend fun setSpeedUnit(unit: WindSpeedUnit) {
        TODO("Not yet implemented")
    }

    override suspend fun updateWeatherAlert(weatherAlert: WeatherAlert) {
        TODO("Not yet implemented")
    }
}
