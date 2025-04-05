package com.example.weatherforcast.MainApp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.weatherforcast.MainApp.ui.WeatherApp
import com.example.weatherforcast.helpyclasses.AppLang
import com.example.weatherforcast.helpyclasses.LanguageUtil
import com.example.weatherforcast.ui.theme.WeatherForcastTheme
import com.google.android.libraries.places.api.Places

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (!Places.isInitialized()) {
            Places.initialize(this, "AIzaSyACknaVvXwj6TEcJeADVCmPxmaJ_qY7HD8")
        }
        if (savedInstanceState == null) {
            val sharedPreferences = getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
            val langName = sharedPreferences.getString("language", AppLang.ENGLISH.name)

            val lang = langName?.let { AppLang.valueOf(it) }

            lang?.let {
                LanguageUtil.changeLanguage(this, it)
            }
        }
        setContent {
            WeatherForcastTheme {
                WeatherApp()
            }
        }
    }



}


