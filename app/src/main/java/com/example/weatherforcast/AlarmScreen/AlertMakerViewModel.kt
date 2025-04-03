package com.example.weatherforcast.AlarmScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherforcast.model.data.WeatherAlert
import com.example.weatherforcast.model.repositories.WeatherDataRepository
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.log

class AlertMakerViewModel (private val dataRepository: WeatherDataRepository): ViewModel(){

    fun checkTimeValidity(selectedDateTime: Long): Boolean {
        val calendar = Calendar.getInstance()
        val currentTime = calendar.timeInMillis / 1000

        return selectedDateTime > currentTime
    }

    fun addAlert(weatherAlert: WeatherAlert,onSuccessful:(Int)->Unit) {
        viewModelScope.launch {
            try {
                val weatherId= dataRepository.addAlertWeather(weatherAlert)
                onSuccessful(weatherId.toInt())
            }catch (e:Exception){
                Log.i("TAG", "addAlert: failure")
            }

        }


    }

}
class AlertMakerViewModelFactory(val dataRepository: WeatherDataRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AlertMakerViewModel(dataRepository) as T
    }
}
