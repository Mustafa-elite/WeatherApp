package com.example.weatherforcast.model.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
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

}