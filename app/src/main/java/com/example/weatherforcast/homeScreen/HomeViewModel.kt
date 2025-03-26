package com.example.weatherforcast.homeScreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherforcast.model.data.CurrentWeatherResponse
import com.example.weatherforcast.model.repositories.WeatherDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import com.example.weatherforcast.model.data.Response
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch

class HomeViewModel(val dataRepository: WeatherDataRepository):ViewModel() {
    private val _weatherResponse:MutableStateFlow<Response> = MutableStateFlow(Response.Loading)
    val weatherResponse=_weatherResponse.asStateFlow()


    private val _viewMessage:MutableLiveData<String> = MutableLiveData()
    val viewMessage:LiveData<String> =_viewMessage

    fun getRecentWeather(lon: Double, lat: Double) {
        viewModelScope.launch {
            try {
                dataRepository.getRemoteWeatherLonLat(lon,lat)
                    .catch {
                        Log.i("TAG", "getRecentWeather:"+it.message)
                        _weatherResponse.emit(Response.Failure(it)) }
                    .collect{
                        Log.i("TAG", "getRecentWeather:success ")
                        _weatherResponse.emit(Response.Success(it))}
            }catch (e:Exception){
                Log.i("TAG", "getRecentWeather: failure catch 2")
                _weatherResponse.emit(Response.Failure(e))
                _viewMessage.postValue("error in retrofit")
            }

        }
    }
}
class HomeViewModelFactory(val dataRepository: WeatherDataRepository):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(dataRepository) as T
    }
}