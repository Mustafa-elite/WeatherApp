package com.example.weatherforcast.helpyclasses

import android.app.Activity
import android.app.LocaleManager
import android.content.Context
import java.util.Locale
import android.content.res.*
import android.os.Build
import android.os.LocaleList
import android.util.Log
import androidx.annotation.RequiresApi


object LanguageUtil {
    fun changeLanguage(activity: Activity,language: AppLang){
        val code=when(language){
            AppLang.ENGLISH -> "en"
            AppLang.ARABIC -> "ar"
        }
        val locale= Locale(code)
        Locale.setDefault(locale)
        val config= Configuration(activity.resources.configuration)
        config.setLocale(locale)

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            setApi33Locale(activity,code)
        }
        else{
            activity.resources.updateConfiguration(config,activity.resources.displayMetrics)
        }
        Log.i("TAG", "changeLanguage: ")
        activity.recreate()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun setApi33Locale(activity: Activity, code: String) {

        val locale = Locale.forLanguageTag(code)
        val localeManager = activity.getSystemService(Context.LOCALE_SERVICE) as LocaleManager
        localeManager.applicationLocales = LocaleList(locale)
    }
}

enum class AppLang {
ENGLISH,
    ARABIC
}
