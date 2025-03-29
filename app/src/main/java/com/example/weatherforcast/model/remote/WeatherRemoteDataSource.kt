package com.example.weatherforcast.model.remote

import android.util.Log
import com.example.weatherforcast.model.data.CurrentWeatherResponse
import com.example.weatherforcast.model.data.DailyWeatherResponse
import com.example.weatherforcast.model.data.ThreeHoursForecastResponse
import com.example.weatherforcast.model.data.WeatherInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext


class WeatherRemoteDataSource (private val weatherService: WeatherService){

    suspend fun getCurrentWeatherLonLat(lon:Double,lat:Double): Flow<CurrentWeatherResponse> {
        Log.i("TAG", "getCurrentWeatherLonLat: ")
        return flowOf(weatherService.getCurrentWeatherByLonLat(lon,lat))
    }
    suspend fun getDailyWeatherLonLat(lon:Double,lat:Double,numOfDays:Int=7): Flow<DailyWeatherResponse> {
        Log.i("TAG", "getDailyWeatherLonLat: ")
        return flowOf(weatherService.getDailyWeatherByLonLat(lon,lat,numOfDays))
    }

    suspend fun getThreeHoursForecastLonLat(lon:Double,lat:Double,numOfForecasts:Int=9): Flow<ThreeHoursForecastResponse> {
        Log.i("TAG", "getDailyWeatherLonLat: ")
        return flowOf(weatherService.getThreeHoursForecast(lon,lat,numOfForecasts))
    }

    suspend fun getWeatherDetails(lon: Double, lat: Double) :WeatherInfo {
        return withContext(Dispatchers.IO) {
            combine(
                getCurrentWeatherLonLat(lon, lat),
                getDailyWeatherLonLat(lon, lat, 9),
                getThreeHoursForecastLonLat(lon, lat, 12)
            ) { currentWeather, dailyWeather, threeHoursWeather ->
                Log.i("TAG", "getRemoteWeatherLonLat: fetching data")
                WeatherInfo(
                    lon = currentWeather.coord.lon,
                    lat = currentWeather.coord.lat,
                    weatherDescription = currentWeather.weather[0].description,
                    windSpeed = currentWeather.wind.speed,
                    temp = currentWeather.main.temp,
                    feelsLike = currentWeather.main.feels_like,
                    minTemp = dailyWeather.list[0].temp.min,
                    maxTemp = dailyWeather.list[0].temp.max,
                    pressure = currentWeather.main.pressure,
                    humidityPercentage = currentWeather.main.humidity,
                    visibility = currentWeather.visibility,
                    cloudyPercentage = currentWeather.clouds.all,
                    calculationTime = currentWeather.dt,
                    timezone = currentWeather.timezone,
                    sunrise = currentWeather.sys.sunrise,
                    sunset = currentWeather.sys.sunset,
                    countryName = currentWeather.sys.country,
                    cityName = currentWeather.name,
                    daysForecast = dailyWeather.list,
                    threeHoursForecast = threeHoursWeather.list,
                    iconInfo = currentWeather.weather[0].iconRes
                )
            }.first()
        }
    }
}