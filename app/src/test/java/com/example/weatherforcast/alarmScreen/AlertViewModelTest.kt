package com.example.weatherforcast.alarmScreen

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherforcast.favouriteScreen.FavViewResponse
import com.example.weatherforcast.favouriteScreen.FavouritesViewModel
import com.example.weatherforcast.model.data.WeatherInfo
import com.example.weatherforcast.model.repositories.WeatherDataRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.Matchers.`is`

@RunWith(AndroidJUnit4::class)
class AlertViewModelTest() {

    lateinit var viewModel: FavouritesViewModel
    lateinit var repo: WeatherDataRepository

    @Before
    fun setup() {
        repo = mockk(relaxed = true)
        viewModel = FavouritesViewModel(repo)
    }
    @Test
    fun getFavData_emitsSuccess_when_repository_returns_data() = runTest {

        val testData = listOf(
            WeatherInfo(weatherId = 1, temp = 20.0, lon = 30.0, lat = 31.0),
            WeatherInfo(weatherId = 2, temp = 22.0, lon = 32.0, lat = 33.0)
        )
        coEvery { repo.getFavData() } returns flowOf(testData)

        viewModel.getFavData()


        val result = viewModel.favWeatherResponse.first()
        assertThat(result, `is`(FavViewResponse.Success(testData)))
    }
    @Test
    fun addFavWeather_emitsFailure_when_repository_throws_exception() = runTest {
        val testException = RuntimeException("database error")

        coEvery {
            repo.getWeatherInfo(any(), any(), isMainLocation = false, isFavourite = true)
        } throws testException

        viewModel.addFavWeather(30.0, 31.0)

        val result = viewModel.favWeatherResponse.first()
        assertThat(result, `is`(FavViewResponse.Failure(testException)))
    }
    @Test
    fun isMainWeather_returnsTrueOnSuccessful() {
        val weatherInfo = WeatherInfo(weatherId = 5, temp = 22.0, lon = 30.0, lat = 31.0)

        every { repo.getMainLocationId() } returns 5

        val result = viewModel.isMainWeather(weatherInfo)

        assertThat(result, `is`(true))

    }

    @Test
    fun isMainWeather_returnsFalseOnFailure() {
        val weatherInfo = WeatherInfo(weatherId = 5, temp = 22.0, lon = 30.0, lat = 31.0)

        every { repo.getMainLocationId() } returns 4

        val result = viewModel.isMainWeather(weatherInfo)

        assertThat(result, `is`(false))
    }



}