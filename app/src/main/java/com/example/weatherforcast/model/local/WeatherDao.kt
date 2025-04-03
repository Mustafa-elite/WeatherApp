package com.example.weatherforcast.model.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.weatherforcast.model.data.WeatherAlert
import com.example.weatherforcast.model.data.WeatherInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Insert
    suspend fun insertWeatherInfo(weatherInfo: WeatherInfo):Long

    @Update
    suspend fun updateWeatherInfo(weatherInfo: WeatherInfo)

    @Query("SELECT * FROM WeatherInfo WHERE weatherId = :weatherId")
    suspend fun getWeatherById(weatherId:Int): WeatherInfo



    @Query("SELECT * FROM WeatherInfo")
    fun getAllWeathers(): Flow<List<WeatherInfo>>

    @Query("DELETE FROM WeatherInfo WHERE weatherId = :weatherId")
    suspend fun removeWeatherById(weatherId: Int)

    @Query("SELECT * FROM WeatherAlerts")
    fun getAlertWeathers():Flow<List<WeatherAlert>>

    @Insert
    suspend fun addAlertWeather(weatherAlert: WeatherAlert):Long

    @Query("DELETE FROM WeatherAlerts WHERE weatherAlertId =:weatherAlertId ")
    suspend fun removeWeatherAlertById(weatherAlertId: Int)

    @Query("SELECT * FROM WeatherAlerts WHERE weatherAlertId=:alertId")
    suspend fun getAlertWeatherById(alertId: Int):WeatherAlert

}