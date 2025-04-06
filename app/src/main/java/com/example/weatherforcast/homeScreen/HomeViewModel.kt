package com.example.weatherforcast.homeScreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherforcast.R
import com.example.weatherforcast.model.data.WeatherInfo
import com.example.weatherforcast.model.repositories.WeatherDataRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch

class HomeViewModel(private val dataRepository: WeatherDataRepository):ViewModel() {
    private val _weatherHomeViewResponse:MutableStateFlow<HomeViewResponse> = MutableStateFlow(HomeViewResponse.Loading)
    val weatherResponse=_weatherHomeViewResponse.asStateFlow()


    private val _viewMessage: MutableSharedFlow<Int> = MutableStateFlow(R.string.Emtpy_String)
    val viewMessage =_viewMessage.asSharedFlow()

    fun getRecentWeather(lon: Double, lat: Double) {
        Log.i("TAG", "getRecentWeather: ")
        viewModelScope.launch {
            try {
                dataRepository.getWeatherInfo(lon,lat, isMainLocation = true, isFavourite = true)
                    .catch {
                        Log.i("TAG", "getRecentWeather:"+it.message)
                        if(it.message=="api_Error"){
                            _viewMessage.emit(R.string.no_internet_or_internal_server_error)
                        }
                        else{

                            _viewMessage.emit(R.string.failed_to_load_weather_data)
                        }
                        _weatherHomeViewResponse.emit(HomeViewResponse.Failure(it)) }
                    .collect{
                        Log.i("TAG", "getRecentWeather:success ")
                        _weatherHomeViewResponse.emit(HomeViewResponse.SuccessWeatherInfo(it))}
            }catch (e:Exception){
                Log.i("TAG", "getRecentWeather: failure catch 2")
                _weatherHomeViewResponse.emit(HomeViewResponse.Failure(e))
                _viewMessage.emit(R.string.error_in_retrofit)
            }

        }
    }

    fun hasDefaultLocation(): Boolean {
        Log.i("TAG", "hasDefaultLocation: ${dataRepository.getMainLocationId()}")
        return dataRepository.getMainLocationId()!=-1

    }

    fun getDefaultLocation() {
        Log.i("TAG", "getDefaultLocation: before getting default location")
        viewModelScope.launch {
            val weatherId=dataRepository.getMainLocationId()
            if(weatherId!=-1){
                try {
                    dataRepository.getWeatherInfoById(weatherId)
                        .catch {
                            if(it.message=="api_Error"){
                                _viewMessage.emit(R.string.no_internet_or_internal_server_error)
                            }
                            else{
                                _viewMessage.emit(R.string.failed_to_load_weather_data)
                            }
                            _weatherHomeViewResponse.emit(HomeViewResponse.Failure(it)) }
                        .collect{
                            Log.i("TAG", "getDefaultLocation: ")
                            _weatherHomeViewResponse.emit(HomeViewResponse.SuccessWeatherInfo(it))}
                }catch (e:Exception){
                    _weatherHomeViewResponse.emit(HomeViewResponse.Failure(e))
                    _viewMessage.emit(R.string.error_in_retrofit)
                }
            }
        }
    }

    fun removeDefaultLocation() {
        viewModelScope.launch {
            dataRepository.setMainWeather(WeatherInfo(weatherId = -1))
        }

    }


}
class HomeViewModelFactory(val dataRepository: WeatherDataRepository):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(dataRepository) as T
    }
}