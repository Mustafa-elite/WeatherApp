package com.example.weatherforcast.model.data

import android.os.Parcelable
import android.util.Log
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.weatherforcast.R
import com.example.weatherforcast.helpyclasses.DateManager
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue


@Entity(tableName = "WeatherInfo")
class WeatherInfo(

    @PrimaryKey(autoGenerate = true)
    val weatherId:Int=0,
    var lon:Double=0.0,
    var lat:Double=0.0,
    var weatherDescription:String="-",
    var windSpeed:Double=0.0,
    var temp:Double=0.0,
    var feelsLike:Double=0.0,
    var minTemp:Double=0.0,
    var maxTemp:Double=0.0,
    var pressure: Int=0,
    var humidityPercentage: Int=0,
    var visibility: Int=0,
    var cloudyPercentage:Int=0,
    var calculationTime:Long=0,
    var timezone: Int=0,
    var sunrise: Long=0,
    var sunset: Long=0,
    var countryName:String="-",
    var cityName:String="-",
    var iconInfo:Int= R.drawable._1d,
    var daysForecast: MutableList<DailyWeatherData> = mutableListOf(),
    var threeHoursForecast: MutableList<ThreeHoursWeatherData> = mutableListOf(),
    var temperatureUnit: TemperatureUnit=TemperatureUnit.KELVIN,
    var windSpeedUnit: WindSpeedUnit=WindSpeedUnit.METER_SECOND
){

    fun convertTemperatureUnit(newTemperatureUnit: TemperatureUnit){
        if(newTemperatureUnit!=this.temperatureUnit)
        {

            temp=convertTemperature(temp,this.temperatureUnit,newTemperatureUnit)
            feelsLike=convertTemperature(feelsLike,this.temperatureUnit,newTemperatureUnit)
            minTemp=convertTemperature(minTemp,this.temperatureUnit,newTemperatureUnit)
            maxTemp=convertTemperature(maxTemp,this.temperatureUnit,newTemperatureUnit)
            daysForecast.forEach{

                it.temp.min=convertTemperature(it.temp.min,this.temperatureUnit,newTemperatureUnit)
                it.temp.max=convertTemperature(it.temp.max,this.temperatureUnit,newTemperatureUnit)
            }
            threeHoursForecast.forEach{
                it.main.temp=convertTemperature(it.main.temp,this.temperatureUnit,newTemperatureUnit)
                it.main.feels_like=convertTemperature(it.main.feels_like,this.temperatureUnit,newTemperatureUnit)
                it.main.temp_max=convertTemperature(it.main.temp_max,this.temperatureUnit,newTemperatureUnit)
                it.main.temp_min=convertTemperature(it.main.temp_min,this.temperatureUnit,newTemperatureUnit)
            }
            this.temperatureUnit=newTemperatureUnit
        }

    }


    private fun convertTemperature(value: Double, from: TemperatureUnit, to: TemperatureUnit): Double {
        val convertedValue = when {
            from == TemperatureUnit.CELSIUS && to == TemperatureUnit.FAHRENHEIT -> value * 9/5 + 32
            from == TemperatureUnit.CELSIUS && to == TemperatureUnit.KELVIN -> value + 273.15
            from == TemperatureUnit.FAHRENHEIT && to == TemperatureUnit.CELSIUS -> (value - 32) * 5/9
            from == TemperatureUnit.FAHRENHEIT && to == TemperatureUnit.KELVIN -> (value - 32) * 5/9 + 273.15
            from == TemperatureUnit.KELVIN && to == TemperatureUnit.CELSIUS -> value - 273.15
            from == TemperatureUnit.KELVIN && to == TemperatureUnit.FAHRENHEIT -> (value - 273.15) * 9/5 + 32
            else -> value
        }
        return (Math.round(convertedValue * 10) / 10.0)
    }

    fun convertWindSpeedUnit(newWindSpeedUnit: WindSpeedUnit){
        if(newWindSpeedUnit!=windSpeedUnit)
        {
            windSpeed=convertWindSpeed(windSpeed,windSpeedUnit,newWindSpeedUnit)
            windSpeedUnit=newWindSpeedUnit
        }
    }
    private fun convertWindSpeed(value: Double, from: WindSpeedUnit, to: WindSpeedUnit): Double {
        val convertedValue = when {
            from == WindSpeedUnit.METER_SECOND && to == WindSpeedUnit.KILOMETER_HOUR -> value * 3.6
            from == WindSpeedUnit.METER_SECOND && to == WindSpeedUnit.MILES_HOUR -> value * 2.237
            from == WindSpeedUnit.KILOMETER_HOUR && to == WindSpeedUnit.METER_SECOND -> value / 3.6
            from == WindSpeedUnit.KILOMETER_HOUR && to == WindSpeedUnit.MILES_HOUR -> value / 1.609
            from == WindSpeedUnit.MILES_HOUR && to == WindSpeedUnit.METER_SECOND -> value / 2.237
            from == WindSpeedUnit.MILES_HOUR && to == WindSpeedUnit.KILOMETER_HOUR -> value * 1.609
            else -> value
        }
        return (Math.round(convertedValue * 10) / 10.0)
    }


    fun getBackgroundImageRes(): Int {
        val isNight= DateManager.isNightTime(calculationTime,sunrise,sunset)
        val imageRes = when {
            weatherDescription.contains("Clear", ignoreCase = true) && !isNight -> R.drawable.clear_weather_morning
            weatherDescription.contains("Clear", ignoreCase = true) && isNight -> R.drawable.clear_weather_night
            weatherDescription.contains("Rain", ignoreCase = true) && !isNight -> R.drawable.rain_weather_morning
            weatherDescription.contains("Rain", ignoreCase = true) && isNight -> R.drawable.rain_weather_night
            weatherDescription.contains("Drizzle", ignoreCase = true) -> R.drawable.drizzle_weather
            weatherDescription.contains("Snow", ignoreCase = true) -> R.drawable.snow_weather
            weatherDescription.contains("Thunderstorm", ignoreCase = true) -> R.drawable.thunderstorm_weather
            else -> R.drawable.else_weather
        }
        return imageRes
    }

    fun fetchCurrentWeatherData(response: CurrentWeatherResponse) {
        lon=response.coord.lon
        lat = response.coord.lat
        weatherDescription = response.weather[0].description
        windSpeed = response.wind.speed
        temp = response.main.temp
        feelsLike = response.main.feels_like
        pressure = response.main.pressure
        humidityPercentage = response.main.humidity
        visibility = response.visibility
        cloudyPercentage = response.clouds.all
        calculationTime = response.dt
        timezone = response.timezone
        sunrise = response.sys.sunrise
        sunset = response.sys.sunset
        countryName = response.sys.country
        cityName = response.name
        iconInfo = response.weather[0].iconRes
    }

    fun fetchThreeHoursWeatherData(response: ThreeHoursForecastResponse) {
        threeHoursForecast = response.list
    }

    fun fetchDailyWeatherData(response: DailyWeatherResponse) {
        minTemp=response.list[0].temp.min
        maxTemp=response.list[0].temp.max
        daysForecast=response.list

    }
}
enum class TemperatureUnit(val unitSymbol: String){
    KELVIN("K"),
    CELSIUS("C"),
    FAHRENHEIT("F")
}
enum class WindSpeedUnit(val unitSymbol: String){
    METER_SECOND("m/s"),
    MILES_HOUR("mph"),
    KILOMETER_HOUR("km/h")
}

