package com.example.weatherforcast.model.local

import android.content.SharedPreferences
import android.util.Log

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

}