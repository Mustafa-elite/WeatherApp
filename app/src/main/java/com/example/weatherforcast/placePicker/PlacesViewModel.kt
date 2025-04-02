package com.example.weatherforcast.placePicker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherforcast.model.repositories.WeatherDataRepository
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class PlacesViewModel (val dataRepository: WeatherDataRepository,val placesClient: PlacesClient): ViewModel() {
    private val _search= MutableSharedFlow<String>(replay = 1)
    private val _predictions = MutableStateFlow<List<AutocompletePrediction>>(emptyList())
    var predictions = _predictions.asStateFlow()

    private val _addressLatLon= MutableStateFlow<LatLng?>(null)
    var addressLatLon=_addressLatLon.asStateFlow()


    private val _addressName= MutableStateFlow<String?>(null)
    var addressName=_addressName.asStateFlow()




    init {
        getQueryAndSearch()
    }
    fun changeQuery(query: String) {
        viewModelScope.launch {
            _search.emit(query)
        }
    }
    fun getQueryAndSearch(){
        viewModelScope.launch {
            _search
                .debounce(500)
                .distinctUntilChanged()
                .collect{
                    dataRepository.getAutoCompleteText(it, placesClient)
                        .addOnCompleteListener{ _predictions.value= it.result.autocompletePredictions }
                        .addOnFailureListener{
                            _predictions.value= emptyList()
                    }
                }
        }
    }

    fun fetchPlaceById(placeId: String, changeCameraOnSuccess: (LatLng) -> Unit) {
        dataRepository.fetchGoogleMapPlaceById(placeId,placesClient).addOnCompleteListener{
            _addressLatLon.value=it.result.place.latLng
            _addressName.value=it.result.place.name
            _predictions.value= emptyList()
            it.result.place.latLng?.let { it1 -> changeCameraOnSuccess(it1) }
        }
    }

    fun updateLocation(latLng: LatLng) {
        _addressLatLon.value=latLng
        _addressName.value="Selected Address"
    }
}

class PlacesViewModelFactory(val dataRepository: WeatherDataRepository, val placesClient: PlacesClient): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlacesViewModel(dataRepository,placesClient) as T
    }
}
