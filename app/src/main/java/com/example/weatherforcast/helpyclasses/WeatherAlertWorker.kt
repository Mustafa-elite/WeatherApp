package com.example.weatherforcast.helpyclasses

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.weatherforcast.MainApp.MainActivity
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
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
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


                val snoozeIntent = Intent(context, SnoozeReceiver::class.java).apply {
                    putExtra("alertid", weatherAlert.weatherAlertId)
                }
                val cancelIntent = Intent(context, CancelReceiver::class.java).apply {
                    putExtra("alertid", weatherAlert.weatherAlertId)
                }

                val snoozePendingIntent = PendingIntent.getBroadcast(
                    context, weatherAlert.weatherAlertId, snoozeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                val cancelPendingIntent = PendingIntent.getBroadcast(
                    context, -weatherAlert.weatherAlertId, cancelIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val weatherInfoJson = Uri.encode(Gson().toJson(it))

                val intent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra("navigate_to", "details")
                    putExtra("weather_info", weatherInfoJson)
                }
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    weatherAlert.weatherAlertId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                val notification=NotificationCompat.Builder(context,chanelId)
                    .setSmallIcon(R.drawable.splash)
                    .setContentTitle(
                        context.getString(
                            R.string.weather_alert,
                            it.cityName,
                            it.countryName
                        ))
                    .setContentText(
                        context.getString(
                            R.string.the_weather_now_is,
                            it.weatherDescription
                        ))
                    .addAction(R.drawable.splash, "snooze", snoozePendingIntent)
                    .addAction(R.drawable.splash, "cancel", cancelPendingIntent)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setSound(soundUri)
                    .build()

                notificationManager.notify(1,notification)

            }
    }
}
class SnoozeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alertId = intent.getIntExtra("alertid", -1)
        if (alertId == -1) return

        val repo = WeatherDataRepository.getInstance(
            WeatherRemoteDataSource(RetrofitHelper.weatherService),
            WeatherLocalDatSource(
                WeatherDatabase.getInstance(context).getWeatherDao(),
                SharedPrefImp(context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE))
            )
        )


        CoroutineScope(Dispatchers.IO).launch {
            val newTime=(System.currentTimeMillis()/1000)+60
            AlertsManager.scheduleWeatherAlert(context,alertId,newTime)
            repo.updateAlertWeatherById(alertId, newTime)

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(1)
        }
    }
}

class CancelReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alertId = intent.getIntExtra("alertid", -1)
        if (alertId == -1) return

        val repo = WeatherDataRepository.getInstance(
            WeatherRemoteDataSource(RetrofitHelper.weatherService),
            WeatherLocalDatSource(
                WeatherDatabase.getInstance(context).getWeatherDao(),
                SharedPrefImp(context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE))
            )
        )

        CoroutineScope(Dispatchers.IO).launch {
            repo.removeAlertWeatherById(alertId)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(1)
        }
    }
}

