package com.example.weatherforcast.repo

import com.example.weatherforcast.model.data.WeatherInfo
import com.example.weatherforcast.model.local.FakeLocalDataSource
import com.example.weatherforcast.model.local.IWeatherLocalDatSource
import com.example.weatherforcast.model.remote.FakeRemoteDataSource
import com.example.weatherforcast.model.remote.IWeatherRemoteDataSource
import com.example.weatherforcast.model.repositories.IWeatherDataRepository
import com.example.weatherforcast.model.repositories.WeatherDataRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.hamcrest.Matchers.`is`

class WeatherRepositoryTest {

    lateinit var localDataSource: IWeatherLocalDatSource
    lateinit var remoteDataSource: IWeatherRemoteDataSource
    lateinit var repo: IWeatherDataRepository

    val localList = mutableListOf(
        WeatherInfo(
            weatherId = 1,
            lat = 30.0,
            lon = 31.0,
            temp = 20.0,
            weatherDescription = "Sunny",
            calculationTime = System.currentTimeMillis()/1000
        ),
        WeatherInfo(
            weatherId = 2,
            lat = 25.0,
            lon = 30.5,
            temp = 18.5,
            weatherDescription = "Cloudy",
            calculationTime = System.currentTimeMillis()/1000
        )
    )

    val remoteList = mutableListOf(
        WeatherInfo(
            weatherId = 3,
            lat = 29.9,
            lon = 31.2,
            temp = 27.3,
            weatherDescription = "Partly Cloudy",
            calculationTime = System.currentTimeMillis()/1000
        ),
        WeatherInfo(
            weatherId = 4,
            lat = 28.0,
            lon = 30.0,
            temp = 33.1,
            weatherDescription = "Clear Sky",
            calculationTime = System.currentTimeMillis()/1000
        )
    )

    @Before
    fun setup() {
        localDataSource = FakeLocalDataSource(localList)
        remoteDataSource = FakeRemoteDataSource(remoteList)
        repo = WeatherDataRepository.getInstance(remoteDataSource, localDataSource)
    }
    @Test
    fun getWeatherInfo_returnsCorrectLocalData() = runTest {
        val lon = 31.01
        val lat = 30.01
        val isMain = false
        val isFav = false

        //will be found in database,so retrieved from local
        val result = repo.getWeatherInfo(lon, lat, isMain, isFav).first()

        assertThat(result.weatherId, `is`(1))
        assertThat(result.temp, `is`(20.0))
        assertThat(result.weatherDescription, `is`("Sunny"))
    }

    @Test
    fun getWeatherInfo_returnsCorrectRemoteData() = runTest {
        val lon = 30.0
        val lat = 28.0
        val isMain = false
        val isFav = false

        //will be found in database,so retrieved from Remote
        val result = repo.getWeatherInfo(lon, lat, isMain, isFav).first()

        assertThat(result.weatherId, `is`(4))
        assertThat(result.temp, `is`(33.1))
        assertThat(result.weatherDescription, `is`("Clear Sky"))
    }
    @Test
    fun getFavData_returnsLocalWeatherList() = runTest {
        val result = repo.getFavData().first()

        assertThat(result.size, `is`(2))
        assertThat(result[0].weatherDescription, `is`("Sunny"))
        assertThat(result[1].weatherDescription, `is`("Cloudy"))
    }


}
