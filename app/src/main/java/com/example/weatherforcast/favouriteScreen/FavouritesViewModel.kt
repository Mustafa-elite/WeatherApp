package com.example.weatherforcast.favouriteScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherforcast.homeScreen.HomeViewResponse
import com.example.weatherforcast.model.repositories.WeatherDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavouritesViewModel (val dataRepository: WeatherDataRepository): ViewModel(){

    private val _favWeatherResponse:MutableStateFlow<FavViewResponse> = MutableStateFlow(FavViewResponse.Loading)
    val favWeatherResponse=_favWeatherResponse.asStateFlow()


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

}

class FavouritesViewModelFactory(val dataRepository: WeatherDataRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavouritesViewModel(dataRepository) as T
    }
}