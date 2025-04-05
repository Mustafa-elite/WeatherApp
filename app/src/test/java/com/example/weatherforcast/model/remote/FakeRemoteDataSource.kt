package com.example.weatherforcast.model.remote

import com.example.weatherforcast.model.data.Clouds
import com.example.weatherforcast.model.data.Coord
import com.example.weatherforcast.model.data.CurrentWeatherResponse
import com.example.weatherforcast.model.data.DailyWeatherData
import com.example.weatherforcast.model.data.DailyWeatherResponse
import com.example.weatherforcast.model.data.ExtraTemp
import com.example.weatherforcast.model.data.Main
import com.example.weatherforcast.model.data.Sys
import com.example.weatherforcast.model.data.ThreeHoursForecastResponse
import com.example.weatherforcast.model.data.ThreeHoursWeatherData
import com.example.weatherforcast.model.data.Weather
import com.example.weatherforcast.model.data.WeatherInfo
import com.example.weatherforcast.model.data.Wind
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeRemoteDataSource(
    private val weatherList: MutableList<WeatherInfo> = mutableListOf()
) : IWeatherRemoteDataSource {

    override suspend fun getCurrentWeatherLonLat(
        lon: Double,
        lat: Double
    ): Flow<CurrentWeatherResponse> {
        return flow {
            emit(
                CurrentWeatherResponse(
                    coord = Coord(lon, lat),
                    weather = listOf(Weather(1, "Clear", "Clear sky", "01d")),
                    main = Main(
                        temp = 25.0,
                        feels_like = 20.0,
                        temp_min = 15.0,
                        temp_max = 30.0,
                        pressure = 100,
                        humidity = 101,
                        sea_level = 102,
                        grnd_level = 103
                    ),
                    wind = Wind(
                        speed = 5.0,
                        deg = 10,
                        gust = 20.0
                    ),
                    name = "Fake City",
                    base = "test",
                    visibility = 10,
                    clouds = Clouds(
                        all = 4
                    ),
                    dt =System.currentTimeMillis()/1000,
                    sys = Sys(
                        country = "Egypt",
                        sunrise = System.currentTimeMillis()/1000,
                        sunset = System.currentTimeMillis()/1000

                    ),
                    timezone = 7200,
                    id = 1,
                    cod = 3
                )
            )
        }
    }

    override suspend fun getDailyWeatherLonLat(
        lon: Double,
        lat: Double,
        numOfDays: Int
    ): Flow<DailyWeatherResponse> {
        val fakeDays = List(numOfDays) {
            DailyWeatherResponse(
                list = mutableListOf(DailyWeatherData(
                    dt = System.currentTimeMillis()/1000,
                    temp = ExtraTemp(
                        min = 15.0,
                        max = 20.0
                    ),
                    weather = listOf(Weather(1, "Clear", "Clear sky", "01d"))
                ))
            )
        }

        return flow {
            emit(
                fakeDays[0] 
            )
        }
    }

    override suspend fun getThreeHoursForecastLonLat(
        lon: Double,
        lat: Double,
        numOfForecasts: Int
    ): Flow<ThreeHoursForecastResponse> {
        // Simulate dummy 3-hour forecasts
        val fakeForecasts = List(numOfForecasts) {
            ThreeHoursForecastResponse(
                cnt = 1,
                list = mutableListOf(ThreeHoursWeatherData(
                    dt = System.currentTimeMillis()/1000,
                    main = Main(
                        temp = 25.0,
                        feels_like = 20.0,
                        temp_min = 15.0,
                        temp_max = 30.0,
                        pressure = 100,
                        humidity = 101,
                        sea_level = 102,
                        grnd_level = 103
                    ),
                    weather = listOf(Weather(1, "Clear", "Clear sky", "01d"))
                ))
            )
        }

        return flow {
            emit(fakeForecasts[0])
        }
    }

    override suspend fun getWeatherDetails(lon: Double, lat: Double): WeatherInfo {

        return weatherList.firstOrNull { it.lon == lon && it.lat == lat }
            ?: WeatherInfo(
                weatherId = 1,
                temp = 28.0,
                lat = lat,
                lon = lon,
                weatherDescription = "Fake Weather"
            )
    }

    override fun getPlacesApiAutoComplete(
        query: String,
        placesClient: PlacesClient
    ): Task<FindAutocompletePredictionsResponse> {
        TODO("Not yet implemented")
    }

    override fun fetchPlaceById(
        placeId: String,
        placesClient: PlacesClient
    ): Task<FetchPlaceResponse> {
        TODO("Not yet implemented")
    }
}
