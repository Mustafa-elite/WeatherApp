package com.example.weatherforcast.model.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weatherforcast.R
import com.example.weatherforcast.model.data.TemperatureUnit
import com.example.weatherforcast.model.data.WeatherInfo
import com.example.weatherforcast.model.data.WindSpeedUnit
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import org.hamcrest.CoreMatchers.`is`
import org.junit.After


@SmallTest
@RunWith(AndroidJUnit4::class)
class WeatherDaoTest {

    private lateinit var database: WeatherDatabase
    private lateinit var dao: WeatherDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).build()
        dao = database.getWeatherDao()

    }


    @Test
    fun insertWeatherInfo_allFields_shouldMatch() = runTest {

        val weatherInfo = WeatherInfo(
            lon = 30.0,
            lat = 31.0,
            weatherDescription = "Clear Sky",
            windSpeed = 5.5,
            temp = 20.0,
            feelsLike = 19.5,
            minTemp = 15.0,
            maxTemp = 28.0,
            pressure = 1013,
            humidityPercentage = 60,
            visibility = 10000,
            cloudyPercentage = 0,
            calculationTime = 1680000000000L,
            timezone = 7200,
            sunrise = 1680001000000L,
            sunset = 1680045000000L,
            countryName = "Egypt",
            cityName = "Cairo",
            iconInfo = R.drawable._1d,
            temperatureUnit = TemperatureUnit.CELSIUS,
            windSpeedUnit = WindSpeedUnit.KILOMETER_HOUR
        )

        val id = dao.insertWeatherInfo(weatherInfo)
        val resultWeather = dao.getWeatherById(id.toInt())

        assertNotNull(resultWeather)
        assertThat(resultWeather.weatherDescription, `is`(weatherInfo.weatherDescription))
        assertThat(resultWeather.windSpeed, `is`(weatherInfo.windSpeed))
        assertThat(resultWeather.temp, `is`(weatherInfo.temp))
        assertThat(resultWeather.feelsLike, `is`(weatherInfo.feelsLike))
        assertThat(resultWeather.minTemp, `is`(weatherInfo.minTemp))
        assertThat(resultWeather.maxTemp, `is`(weatherInfo.maxTemp))
        assertThat(resultWeather.pressure, `is`(weatherInfo.pressure))
        assertThat(resultWeather.humidityPercentage, `is`(weatherInfo.humidityPercentage))
        assertThat(resultWeather.visibility, `is`(weatherInfo.visibility))
        assertThat(resultWeather.cloudyPercentage, `is`(weatherInfo.cloudyPercentage))
        assertThat(resultWeather.calculationTime, `is`(weatherInfo.calculationTime))
        assertThat(resultWeather.timezone, `is`(weatherInfo.timezone))
        assertThat(resultWeather.sunrise, `is`(weatherInfo.sunrise))
        assertThat(resultWeather.sunset, `is`(weatherInfo.sunset))
        assertThat(resultWeather.countryName, `is`(weatherInfo.countryName))
        assertThat(resultWeather.cityName, `is`(weatherInfo.cityName))
        assertThat(resultWeather.iconInfo, `is`(weatherInfo.iconInfo))
        assertThat(resultWeather.temperatureUnit, `is`(weatherInfo.temperatureUnit))
        assertThat(resultWeather.windSpeedUnit, `is`(weatherInfo.windSpeedUnit))
    }
    @Test
    fun updateWeatherInfo_shouldUpdateFieldsCorrectly() = runTest {
        val original = WeatherInfo(
            lon = 30.0,
            lat = 31.0,
            weatherDescription = "Cloudy",
            temp = 22.0,
            cityName = "Cairo",
            windSpeed = 0.0
        )
        val id = dao.insertWeatherInfo(original).toInt()

        val updated = WeatherInfo(
            weatherId = id,
            lon = 35.0,
            lat = 36.0,
            weatherDescription = "Sunny",
            temp = 28.0,
            cityName = "Alexandria"
        )

        dao.updateWeatherInfo(updated)

        val result = dao.getWeatherById(id)

        assertNotNull(result)
        assertThat(result.lon, `is`(35.0))
        assertThat(result.lat, `is`(36.0))
        assertThat(result.weatherDescription, `is`("Sunny"))
        assertThat(result.temp, `is`(28.0))
        assertThat(result.cityName, `is`("Alexandria"))

        assertThat(result.windSpeed, `is`(0.0))
    }
    @After
    fun tearDown() {
        database.close()
    }


}
