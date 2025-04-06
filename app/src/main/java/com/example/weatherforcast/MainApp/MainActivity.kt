package com.example.weatherforcast.MainApp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.weatherforcast.MainApp.ui.Screen
import com.example.weatherforcast.MainApp.ui.WeatherApp
import com.example.weatherforcast.helpyclasses.AppLang
import com.example.weatherforcast.helpyclasses.LanguageUtil
import com.google.android.libraries.places.api.Places
import java.text.NumberFormat
import java.util.Locale

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
        val weatherInfoJson = intent.getStringExtra("weather_info")

        setContent {
            val startDestination = if (!weatherInfoJson.isNullOrEmpty()) {
                Screen.Details.createRoute(weatherInfoJson)
            } else {
                Screen.Splash.rout
            }

            WeatherApp(startDestination = startDestination)
        }
    }



}
fun Double.getLocaleString(): String {
    val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
    return numberFormat.format(this)
}

fun Int.getLocaleString(): String {
    val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
    return numberFormat.format(this)
}
fun String.getLocaleTempUnit(): String {
    return when (this.uppercase()) {
        "C" -> if (Locale.getDefault().language == "ar") "°س" else "°C"
        "F" -> if (Locale.getDefault().language == "ar") "°ف" else "°F"
        "K" -> if (Locale.getDefault().language == "ar") "°ك" else "°K"
        else -> this
    }
}
fun String.getLocaleWindSpeedUnit(): String {
    return when (this.lowercase()) {
        "km/h" -> if (Locale.getDefault().language == "ar") "كم/س" else "km/h"
        "mph" -> if (Locale.getDefault().language == "ar") "ميل/س" else "mph"
        "m/s" -> if (Locale.getDefault().language == "ar") "م/ث" else "m/s"
        else -> this
    }
}
fun String.getLocaleWeatherDescription(): String {
    return when (this.lowercase()) {
        "clear sky","sky is clear" -> if (Locale.getDefault().language == "ar") "سماء صافية" else "Clear sky"
        "few clouds" -> if (Locale.getDefault().language == "ar") "قليل من الغيوم" else "Few clouds"
        "scattered clouds" -> if (Locale.getDefault().language == "ar") "غيوم متفرقة" else "Scattered clouds"
        "broken clouds" -> if (Locale.getDefault().language == "ar") "غيوم متكسرة" else "Broken clouds"
        "shower rain" -> if (Locale.getDefault().language == "ar") "أمطار خفيفة" else "Shower rain"
        "rain" -> if (Locale.getDefault().language == "ar") "مطر" else "Rain"
        "thunderstorm" -> if (Locale.getDefault().language == "ar") "عاصفة رعدية" else "Thunderstorm"
        "snow" -> if (Locale.getDefault().language == "ar") "ثلج" else "Snow"
        "mist" -> if (Locale.getDefault().language == "ar") "ضباب" else "Mist"
        "haze" -> if (Locale.getDefault().language == "ar") "دخان" else "Haze"
        "dust" -> if (Locale.getDefault().language == "ar") "غبار" else "Dust"
        "fog" -> if (Locale.getDefault().language == "ar") "ضباب كثيف" else "Fog"
        "sand" -> if (Locale.getDefault().language == "ar") "رمال" else "Sand"
        "ash" -> if (Locale.getDefault().language == "ar") "رماد" else "Ash"
        "overcast clouds" -> if (Locale.getDefault().language == "ar") "غيوم غائمة" else "Overcast clouds"
        else -> this
    }
}

