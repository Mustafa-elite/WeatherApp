package com.example.weatherforcast.model.data

sealed class Response {
    data object Loading:Response()
    data class Success(val data:WeatherInfo):Response()
    data class Failure(val data:Throwable):Response()

}