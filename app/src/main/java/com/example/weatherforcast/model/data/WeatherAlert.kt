package com.example.weatherforcast.model.data

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weatherforcast.helpyclasses.LocationUtil


@Entity(tableName = "WeatherAlerts")
class WeatherAlert(
    @PrimaryKey(autoGenerate = true)
    var weatherAlertId:Int=0,
    var lon:Double,
    var lat:Double,
    var countryName:String="",
    var cityName:String="",
    var alertDateTime:Long
) {
    fun updateLocation(context: Context) {
        val (city, country) = LocationUtil.getCityAndCountry(lat, lon, context)
        cityName = city
        countryName = country
    }
}