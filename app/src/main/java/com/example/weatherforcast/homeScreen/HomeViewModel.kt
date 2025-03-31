package com.example.weatherforcast.homeScreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherforcast.model.repositories.WeatherDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch

class HomeViewModel(val dataRepository: WeatherDataRepository):ViewModel() {
    private val _weatherHomeViewResponse:MutableStateFlow<HomeViewResponse> = MutableStateFlow(HomeViewResponse.Loading)
    val weatherResponse=_weatherHomeViewResponse.asStateFlow()


    private val _viewMessage:MutableLiveData<String> = MutableLiveData()
    val viewMessage:LiveData<String> =_viewMessage

    fun getRecentWeather(lon: Double, lat: Double) {
        viewModelScope.launch {
            try {
                dataRepository.getWeatherInfo(lon,lat)
                    .catch {
                        Log.i("TAG", "getRecentWeather:"+it.message)
                        _weatherHomeViewResponse.emit(HomeViewResponse.Failure(it)) }
                    .collect{
                        Log.i("TAG", "getRecentWeather:success ")
                        _weatherHomeViewResponse.emit(HomeViewResponse.SuccessWeatherInfo(it))}
            }catch (e:Exception){
                Log.i("TAG", "getRecentWeather: failure catch 2")
                _weatherHomeViewResponse.emit(HomeViewResponse.Failure(e))
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