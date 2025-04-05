package com.example.weatherforcast.model.remote

import android.util.Log
import com.example.weatherforcast.model.data.CurrentWeatherResponse
import com.example.weatherforcast.model.data.DailyWeatherResponse
import com.example.weatherforcast.model.data.ThreeHoursForecastResponse
import com.example.weatherforcast.model.data.WeatherInfo
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext


class WeatherRemoteDataSource (private val weatherService: WeatherService) :
    IWeatherRemoteDataSource {

    override suspend fun getCurrentWeatherLonLat(lon:Double, lat:Double): Flow<CurrentWeatherResponse> {
        Log.i("TAG", "getCurrentWeatherLonLat: ")
        return flowOf(weatherService.getCurrentWeatherByLonLat(lon,lat))
    }
    override suspend fun getDailyWeatherLonLat(lon:Double, lat:Double, numOfDays:Int): Flow<DailyWeatherResponse> {
        Log.i("TAG", "getDailyWeatherLonLat: ")
        return flowOf(weatherService.getDailyWeatherByLonLat(lon,lat,numOfDays))
    }

    override suspend fun getThreeHoursForecastLonLat(lon:Double, lat:Double, numOfForecasts:Int): Flow<ThreeHoursForecastResponse> {
        Log.i("TAG", "getDailyWeatherLonLat: ")
        return flowOf(weatherService.getThreeHoursForecast(lon,lat,numOfForecasts))
    }

    override suspend fun getWeatherDetails(lon: Double, lat: Double) :WeatherInfo {
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

    override fun getPlacesApiAutoComplete(query: String, placesClient: PlacesClient): Task<FindAutocompletePredictionsResponse> {
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .build()

        return placesClient.findAutocompletePredictions(request)

    }

    override fun fetchPlaceById(placeId: String, placesClient: PlacesClient): Task<FetchPlaceResponse> {
        val request= FetchPlaceRequest.newInstance(placeId, listOf(Place.Field.LAT_LNG, Place.Field.NAME))
        return placesClient.fetchPlace(request)

    }
}