package com.example.weatherforcast.model.remote

import com.example.weatherforcast.model.data.CurrentWeatherResponse
import com.example.weatherforcast.model.data.DailyWeatherResponse
import com.example.weatherforcast.model.data.ThreeHoursForecastResponse
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
                                        @Query("cnt") days: Int,
                                        @Query("appid") apiKey:String = APP_ID):DailyWeatherResponse

    @GET("forecast")
    suspend fun getThreeHoursForecast(@Query("lon") longitude:Double,
                                      @Query("lat") latitude:Double,
                                      @Query("cnt") days: Int,
                                      @Query("appid") apiKey:String = APP_ID): ThreeHoursForecastResponse

    companion object{
        private const val APP_ID="83eb33e422a6ead3fdbec060c161e449"
    }
}

/*
?lat=57&lon=-2.15&cnt=3&appid={API key}

 */
object RetrofitHelper{
    private const val BASE_URL="https://api.openweathermap.org/data/2.5/"
    val retrofitInstance= Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val weatherService= retrofitInstance.create(WeatherService::class.java)

}
