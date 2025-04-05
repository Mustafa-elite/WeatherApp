package com.example.weatherforcast.helpyclasses

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DateManager {
    companion object{
        fun secondsToWrittenDate(secondsUTC: Long, timezone: Int): String {
            val date= Date((secondsUTC+timezone)*1000)
            val dateFormat=SimpleDateFormat("EEE,dd MMM", Locale.getDefault())
            dateFormat.timeZone= TimeZone.getTimeZone("UTC")
            return(dateFormat.format(date))
        }

        fun getTimeFromSeconds(secondsUTC: Long, timezone: Int): String {
            val date=Date((secondsUTC+timezone)*1000)
            val timeFormat=SimpleDateFormat("hh:mm a", Locale.getDefault())
            timeFormat.timeZone= TimeZone.getTimeZone("UTC")
            return timeFormat.format(date)
        }

        fun getDayMonthFromSeconds(secondsUTC: Long, timezone: Int): String {
            val date=Date((secondsUTC+timezone)*1000)
            val timeFormat=SimpleDateFormat("dd/MM", Locale.getDefault())
            timeFormat.timeZone= TimeZone.getTimeZone("UTC")
            return timeFormat.format(date)


        }
        fun getDayFromSeconds(secondsUTC: Long, timezoneShiftSeconds: Int): String {
            // Apply the time shift to the UTC seconds
            val date = Date((secondsUTC + timezoneShiftSeconds) * 1000)

            val timeFormat = SimpleDateFormat("EEE", Locale.getDefault())
            timeFormat.timeZone = TimeZone.getTimeZone("UTC")

            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            val today = calendar.get(Calendar.DAY_OF_YEAR)
            val year = calendar.get(Calendar.YEAR)

            calendar.time = date
            val givenDay = calendar.get(Calendar.DAY_OF_YEAR)
            val givenYear = calendar.get(Calendar.YEAR)

            return if (givenDay == today && givenYear == year){
                if (Locale.getDefault()==Locale("en")) "Today" else "اليوم"
            } else timeFormat.format(date)
        }

        fun isNightTime(currentTimeUTC: Long, sunrise: Long, sunset: Long): Boolean {
            return currentTimeUTC < sunrise || currentTimeUTC > sunset
        }
        fun hasDurationPassed(timeSecondsUTC: Long, days: Int = 0, hours: Int = 0, minutes: Int = 0): Boolean {
            val currentUtcTime = System.currentTimeMillis() / 1000
            val timeDifference = (days * 24L * 3600) + (hours * 3600L) + (minutes * 60L)
            return currentUtcTime >= (timeSecondsUTC + timeDifference)
        }

        fun hasTimePassed(secondsUTC: Long): Boolean {
            val currentUtcTime = System.currentTimeMillis() / 1000
            return currentUtcTime>=secondsUTC
        }
        fun getLocalDateTimeFromSeconds(seconds: Long): String {

            val date = Date(seconds * 1000)

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getDefault()
            return dateFormat.format(date)
        }
        fun getCurrentTime(timezoneShiftSeconds: Int): String {
            val currentUtcTimeMillis = System.currentTimeMillis()
            val shiftedTimeMillis = currentUtcTimeMillis + timezoneShiftSeconds * 1000L
            val date = Date(shiftedTimeMillis)

            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            timeFormat.timeZone = TimeZone.getTimeZone("UTC")
            return timeFormat.format(date)
        }

    }
}