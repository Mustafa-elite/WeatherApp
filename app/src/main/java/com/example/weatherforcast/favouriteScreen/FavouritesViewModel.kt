package com.example.weatherforcast.favouriteScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherforcast.homeScreen.HomeViewResponse
import com.example.weatherforcast.model.data.WeatherInfo
import com.example.weatherforcast.model.repositories.WeatherDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavouritesViewModel (private val dataRepository: WeatherDataRepository): ViewModel(){

    private val _favWeatherResponse:MutableStateFlow<FavViewResponse> = MutableStateFlow(FavViewResponse.Loading)
    val favWeatherResponse=_favWeatherResponse.asStateFlow()


    private val _viewMessage: MutableSharedFlow<String> = MutableStateFlow("")
    val viewMessage =_viewMessage.asSharedFlow()

    fun getFavData(){
        viewModelScope.launch {
            try {
                dataRepository.getFavData()
                    .catch {
                        _favWeatherResponse.emit(FavViewResponse.Failure(it)) }
                    .collect{
                       _favWeatherResponse.emit(FavViewResponse.Success(it))}
            }catch (e:Exception){
                Log.i("TAG", "getRecentWeather: failure catch 2")
                _favWeatherResponse.emit(FavViewResponse.Failure(e))
                //_viewMessage.postValue("error in retrofit")
            }
        }
    }
    fun addFavWeather(lon:Double,lat:Double){
        viewModelScope.launch {
            try {
                dataRepository.getWeatherInfo(lon,lat, isMainLocation = false, isFavourite = true)
                    .catch {
                        Log.i("TAG", "getRecentWeather:"+it.message)
                        _favWeatherResponse.emit(FavViewResponse.Failure(it)) }
                    .collect{
                        Log.i("TAG", "getRecentWeather:success ")
                        //_weatherHomeViewResponse.emit(HomeViewResponse.SuccessWeatherInfo(it))
                    }
            }catch (e:Exception){
                Log.i("TAG", "getRecentWeather: failure catch 2")
                _favWeatherResponse.emit(FavViewResponse.Failure(e))
                //_viewMessage.postValue("error in retrofit")
            }
        }

    }

    fun isMainWeather(weatherInfo: WeatherInfo): Boolean {
        return dataRepository.getMainLocationId()==weatherInfo.weatherId
    }

    fun removeFavItem(weatherInfo: WeatherInfo,onResult:(Boolean)->String) {
        viewModelScope.launch {
            try {
                dataRepository.removeFavWeatherById(weatherInfo.weatherId)
                //_viewMessage.emit("Deleted Successfully")
                _viewMessage.emit(onResult(true))
            }catch (e:Exception){
                _viewMessage.emit(onResult(false))
                //_viewMessage.emit("Internal Problem")

            }
        }

    }

    fun makeDefaultWeather(weatherInfo: WeatherInfo) {
        Log.i("TAG", "makeDefaultWeather: ")
        viewModelScope.launch {
            dataRepository.setMainWeather(weatherInfo)
        }

    }

}

class FavouritesViewModelFactory(val dataRepository: WeatherDataRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavouritesViewModel(dataRepository) as T
    }
}