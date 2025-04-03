package com.example.weatherforcast.helpyclasses

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.weatherforcast.R
import com.example.weatherforcast.model.data.WeatherAlert
import com.example.weatherforcast.model.data.WeatherInfo
import com.example.weatherforcast.model.local.SharedPrefImp
import com.example.weatherforcast.model.local.WeatherDatabase
import com.example.weatherforcast.model.local.WeatherLocalDatSource
import com.example.weatherforcast.model.remote.RetrofitHelper
import com.example.weatherforcast.model.remote.WeatherRemoteDataSource
import com.example.weatherforcast.model.remote.WeatherService
import com.example.weatherforcast.model.repositories.WeatherDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.withContext

class WeatherAlertWorker(_context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(_context, workerParameters) {
    val context = _context

    val repo = WeatherDataRepository.getInstance(
        WeatherRemoteDataSource(RetrofitHelper.weatherService),
        WeatherLocalDatSource(
            WeatherDatabase.getInstance(context).getWeatherDao(),
            SharedPrefImp(context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE))
        )
    )

    override suspend fun doWork(): Result {
        val alertId=inputData.getInt("alertid",-1)
        if(alertId==-1){
            return Result.failure()
        }
        val weatherAlert=repo.getAlertWeatherById(alertId)

        showNotification(context,weatherAlert)
        return Result.success()
    }

    private suspend fun showNotification(context: Context, weatherAlert: WeatherAlert) {
        repo.getWeatherInfo(
            weatherAlert.lon,
            weatherAlert.lat,
            isMainLocation = false,
            isFavourite = false
        )
            .catch { it.stackTrace }
            .collect{
                val chanelId="Weather_Alerts"
                val notificationManager=context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val soundUri = Uri.parse("android.resource://${context.packageName}/raw/alert_sound")

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                    val channel=NotificationChannel(chanelId,"Weather Alerts",NotificationManager.IMPORTANCE_HIGH).apply {
                        setSound(
                            soundUri,
                            AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .build())
                    }
                    notificationManager.createNotificationChannel(channel)
                }
                val notification=NotificationCompat.Builder(context,chanelId)
                    .setSmallIcon(R.drawable.splash)
                    .setContentTitle(it.cityName+", "+it.countryName+" Weather Alert")
                    .setContentText("The Weather Now is "+ it.weatherDescription)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setSound(soundUri)
                    .build()

                notificationManager.notify(1,notification)
                repo.removeAlertWeatherById(weatherAlert.weatherAlertId)
            }
    }
}