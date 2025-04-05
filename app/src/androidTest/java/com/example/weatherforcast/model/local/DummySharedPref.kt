package com.example.weatherforcast.model.local

import com.example.weatherforcast.model.data.TemperatureUnit
import com.example.weatherforcast.model.data.WindSpeedUnit

class DummySharedPref : SharedPrefInterface {
    override fun addMainWeather(weatherId: Int) {
        TODO("Not yet implemented")
    }

    override fun getMainWeatherId(): Int {
        TODO("Not yet implemented")
    }

    override fun removeMainWeather(weatherId: Int) {
        TODO("Not yet implemented")
    }

    override fun getLanguage(): String {
        TODO("Not yet implemented")
    }

    override fun setLanguage(language: String) {
        TODO("Not yet implemented")
    }

    override fun getTemperatureUnit(): String {
        TODO("Not yet implemented")
    }

    override fun setTemperatureUnit(unit: TemperatureUnit) {
        TODO("Not yet implemented")
    }

    override fun getSpeedUnit(): String {
        TODO("Not yet implemented")
    }

    override fun setSpeedUnit(unit: WindSpeedUnit) {
        TODO("Not yet implemented")
    }
}