package com.example.weatherforcast.AlarmScreen

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherforcast.model.data.WeatherAlert
import com.example.weatherforcast.model.repositories.WeatherDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AlertViewModel (private val dataRepository: WeatherDataRepository): ViewModel(){
    private val _alertViewResponse=MutableStateFlow<AlertViewResponse>(AlertViewResponse.Loading)
    val alertViewResponse=_alertViewResponse.asStateFlow()
    fun getAlerts() {
        viewModelScope.launch {
            try {
                dataRepository.getAlertsWeather()
                    .catch { _alertViewResponse.emit(AlertViewResponse.Failure(it))}
                    .collect{ _alertViewResponse.emit(AlertViewResponse.Success(it)) }

            }catch (e:Exception){
                _alertViewResponse.emit(AlertViewResponse.Failure(e))
            }
        }

    }

    fun deleteAlert(weatherAlert: WeatherAlert,onSuccessful:(Int)->Unit) {
        viewModelScope.launch {
            try {
                dataRepository.removeAlertWeatherById(weatherAlert.weatherAlertId)
                onSuccessful(weatherAlert.weatherAlertId)
                Log.i("TAG", "deleteAlert: deleted succesfully")
            }catch (e:Exception){
                Log.i("TAG", "deleteAlert: failed")
            }
        }

    }

}

class AlertViewModelFactory(val dataRepository: WeatherDataRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AlertViewModel(dataRepository) as T
    }
}
