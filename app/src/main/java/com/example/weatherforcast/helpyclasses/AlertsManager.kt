package com.example.weatherforcast.helpyclasses

import android.content.Context
import android.util.Log
import androidx.work.*
import java.util.concurrent.TimeUnit

class AlertsManager {

    companion object{

        fun scheduleWeatherAlert(context: Context, alertId: Int, alertTime: Long) {
            val currentTime = System.currentTimeMillis()/1000
            val delay = alertTime - currentTime

            if (delay <= 0) return

            Log.i("TAG", "scheduleWeatherAlert: delay:$delay")
            val workRequest = OneTimeWorkRequestBuilder<WeatherAlertWorker>()
                .setInitialDelay(delay, TimeUnit.SECONDS)
                .setInputData(workDataOf("alertid" to alertId))
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "weather_alert_$alertId",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
        fun cancelWeatherAlert(context: Context, alertId: Int) {
            WorkManager.getInstance(context).cancelUniqueWork("weather_alert_$alertId")
        }

    }
}