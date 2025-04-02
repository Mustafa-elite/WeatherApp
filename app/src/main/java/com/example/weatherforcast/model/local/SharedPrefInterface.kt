package com.example.weatherforcast.model.local

interface SharedPrefInterface {
    abstract fun addMainWeather(weatherId: Int)
    abstract fun getMainWeatherId(): Int
    abstract fun removeMainWeather(weatherId: Int)

}
