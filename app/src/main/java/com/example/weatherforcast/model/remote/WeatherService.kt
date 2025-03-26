package com.example.weatherforcast.model.remote

import com.example.weatherforcast.model.data.CurrentWeatherResponse
import com.example.weatherforcast.model.data.DailyWeatherResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("weather")
    suspend fun getCurrentWeatherByLonLat(@Query("lon") longitude:Double,
                                          @Query("lat") latitude:Double,
                                          @Query("appid") apiKey:String = APP_ID):CurrentWeatherResponse

    @GET("forecast/daily")
    suspend fun getDailyWeatherByLonLat(@Query("lon") longitude:Double,
                                        @Query("lat") latitude:Double,
                                        @Query("cnt") days: Int = 7,
                                        @Query("appid") apiKey:String = APP_ID):DailyWeatherResponse

    companion object{
        private const val APP_ID=""
    }
}


object RetrofitHelper{
    private const val BASE_URL="https://api.openweathermap.org/data/2.5/"
    val retrofitInstance= Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val weatherService= retrofitInstance.create(WeatherService::class.java)

}
