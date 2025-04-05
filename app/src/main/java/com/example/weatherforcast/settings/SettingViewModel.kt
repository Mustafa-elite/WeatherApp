package com.example.weatherforcast.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherforcast.model.data.TemperatureUnit
import com.example.weatherforcast.model.data.WindSpeedUnit
import com.example.weatherforcast.model.repositories.WeatherDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingViewModel(private val dataRepository: WeatherDataRepository): ViewModel() {

    private val _language = MutableStateFlow(dataRepository.getLanguage())
    val language=_language.asStateFlow()

    private val _temperatureUnit = MutableStateFlow(dataRepository.getTemperatureUnit())
    val temperatureUnit= _temperatureUnit.asStateFlow()

    private val _speedUnit = MutableStateFlow(dataRepository.getSpeedUnit())
    val speedUnit= _speedUnit.asStateFlow()

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            dataRepository.setLanguage(lang)
            _language.value = lang
        }

    }

    fun setTemperatureUnit(unit: TemperatureUnit) {
        viewModelScope.launch {
            dataRepository.setTemperatureUnit(unit)
            _temperatureUnit.value = unit
        }
    }

    fun setSpeedUnit(unit: WindSpeedUnit) {
        viewModelScope.launch {
            dataRepository.setSpeedUnit(unit)
            _speedUnit.value = unit
        }
    }
}

class SettingViewModelFactory(val dataRepository: WeatherDataRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingViewModel(dataRepository) as T
    }
}