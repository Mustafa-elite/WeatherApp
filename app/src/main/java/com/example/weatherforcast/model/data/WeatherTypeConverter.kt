package com.example.weatherforcast.model.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WeatherTypeConverter {

    private val gson=Gson()
    @TypeConverter
    fun fromDailyListToJson(list: MutableList<DailyWeatherData>): String{
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromJsonToDailyList(jsonString:String): MutableList<DailyWeatherData>{
        val type= object :TypeToken<MutableList<DailyWeatherData>>(){}.type
        return gson.fromJson(jsonString,type)
    }
    @TypeConverter
    fun from3hoursListToJson(list: MutableList<ThreeHoursWeatherData>): String{
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromJsonTo3hoursList(jsonString:String): MutableList<ThreeHoursWeatherData>{
        val type= object :TypeToken<MutableList<ThreeHoursWeatherData>>(){}.type
        return gson.fromJson(jsonString,type)
    }

    @TypeConverter
    fun fromTemperatureUnit(value: TemperatureUnit): String {
        return value.name
    }

    @TypeConverter
    fun toTemperatureUnit(value: String): TemperatureUnit {
        return TemperatureUnit.valueOf(value)
    }

    @TypeConverter
    fun fromWindSpeedUnit(value: WindSpeedUnit): String {
        return value.name
    }

    @TypeConverter
    fun toWindSpeedUnit(value: String): WindSpeedUnit {
        return WindSpeedUnit.valueOf(value)
    }
}