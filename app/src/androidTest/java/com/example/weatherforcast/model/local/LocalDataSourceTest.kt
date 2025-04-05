package com.example.weatherforcast.model.local


import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weatherforcast.model.data.TemperatureUnit
import com.example.weatherforcast.model.data.WeatherInfo
import com.example.weatherforcast.model.data.WindSpeedUnit
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@SmallTest
@RunWith(AndroidJUnit4::class)
class LocalDataSourceTest {

    private lateinit var database: WeatherDatabase
    private lateinit var dao: WeatherDao
    private lateinit var localDatSource: IWeatherLocalDatSource

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.getWeatherDao()
        localDatSource=WeatherLocalDatSource(dao,DummySharedPref())
    }
    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun saveWeather_savesCorrectly() = runTest {
        val weatherInfo = WeatherInfo(
            temp = 20.0,
            lon = 30.0,
            lat = 31.0,
            weatherDescription = "Sunny"
        )

        val id = localDatSource.saveWeather(weatherInfo)
        val saved = localDatSource.getWeatherById(id.toInt())

        assertThat(saved.temp, `is`(20.0))
        assertThat(saved.weatherDescription, `is`("Sunny"))
        assertThat(saved.lat, `is`(31.0))
        assertThat(saved.lon, `is`(30.0))
    }
    @Test
    fun updateWeather_updatesCorrectly() = runTest {
        val original = WeatherInfo(
            temp = 20.0,
            lon = 30.0,
            lat = 31.0,
            weatherDescription = "Sunny"
        )

        val id = localDatSource.saveWeather(original)

        val updated = WeatherInfo(
            weatherId = id.toInt(),
            temp = 25.0,
            weatherDescription = "Cloudy"
        )
        localDatSource.updateWeather(updated)

        val afterUpdate = localDatSource.getWeatherById(id.toInt())
        assertThat(afterUpdate.temp, `is`(25.0))
        assertThat(afterUpdate.weatherDescription, `is`("Cloudy"))
    }
}


