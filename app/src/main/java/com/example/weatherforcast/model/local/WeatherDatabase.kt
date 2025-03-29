package com.example.weatherforcast.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.weatherforcast.model.data.WeatherInfo
import com.example.weatherforcast.model.data.WeatherTypeConverter

@Database(entities = arrayOf(WeatherInfo::class), version = 1, exportSchema = false)
@TypeConverters(WeatherTypeConverter::class)
abstract class WeatherDatabase:RoomDatabase() {

    abstract fun getWeatherDao():WeatherDao
    companion object{
        @Volatile
        private var weatherDatabase:WeatherDatabase? = null
        fun getInstance(context:Context): WeatherDatabase {
            return weatherDatabase ?: synchronized(this){
                val temp=Room.databaseBuilder(context,WeatherDatabase::class.java,"Weather").build()
                weatherDatabase=temp
                temp
            }
        }
    }

}