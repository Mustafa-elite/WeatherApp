package com.example.weatherforcast.favouriteScreen.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.example.weatherforcast.model.data.WeatherInfo

@Composable
fun FavList(
    dataList: List<WeatherInfo>,
    checkIsDefault:(WeatherInfo)->Boolean,
    favItemOnDelete:(WeatherInfo)->Unit,
    favItemOnSetDefault:(WeatherInfo)->Unit,
    favItemClick: (WeatherInfo) -> Unit) {
    LazyColumn {
        items(dataList) {
            WeatherItem(it, checkIsDefault(it), favItemOnDelete, favItemOnSetDefault, favItemClick)
        }
    }
}