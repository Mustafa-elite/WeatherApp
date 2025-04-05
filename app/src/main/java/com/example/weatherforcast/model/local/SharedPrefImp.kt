package com.example.weatherforcast.model.local

import android.content.SharedPreferences
import android.util.Log
import com.example.weatherforcast.helpyclasses.AppLang
import com.example.weatherforcast.model.data.TemperatureUnit
import com.example.weatherforcast.model.data.WindSpeedUnit

class SharedPrefImp(private val sharedPreferences: SharedPreferences) :SharedPrefInterface{
    override fun addMainWeather(weatherId: Int) {
        sharedPreferences.edit().putInt("MAIN_WEATHER_ID", weatherId).apply()
        Log.i("TAG", "addMainWeather: "+sharedPreferences.getInt("MAIN_WEATHER_ID",-1))
    }

    override fun getMainWeatherId(): Int {
        return sharedPreferences.getInt("MAIN_WEATHER_ID",-1)
    }

    override fun removeMainWeather(weatherId: Int) {
        sharedPreferences.edit().remove("MAIN_WEATHER_ID").apply()
    }


    override fun getLanguage():String= sharedPreferences.getString("language", AppLang.ENGLISH.name) ?: AppLang.ENGLISH.name
    override fun setLanguage(language: String) {
        sharedPreferences.edit().putString("language", language).apply()
    }

    override fun getTemperatureUnit(): String = sharedPreferences.getString("temperature_unit",  TemperatureUnit.CELSIUS.name) ?: TemperatureUnit.CELSIUS.name
    override fun setTemperatureUnit(unit: TemperatureUnit) {
        sharedPreferences.edit().putString("temperature_unit", unit.name).apply()
    }

    override fun getSpeedUnit(): String = sharedPreferences.getString("speed_unit", WindSpeedUnit.KILOMETER_HOUR.name) ?: WindSpeedUnit.KILOMETER_HOUR.name
    override fun setSpeedUnit(unit: WindSpeedUnit) {
        sharedPreferences.edit().putString("speed_unit", unit.name).apply()
    }

}