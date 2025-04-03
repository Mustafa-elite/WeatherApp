package com.example.weatherforcast.helpyclasses

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DateManager {
    companion object{
        fun SecondsToWrittenDate(seconds:Long): String {
            val date= Date(seconds*1000)
            val dateFormat=SimpleDateFormat("EEE,dd MMM", Locale.ENGLISH)
            dateFormat.timeZone= TimeZone.getTimeZone("UTC")
            return(dateFormat.format(date))
        }

        fun getTimeFromSeconds(seconds: Long): String {
            val date=Date(seconds*1000)
            val timeFormat=SimpleDateFormat("hh:mm a", Locale.ENGLISH)
            timeFormat.timeZone= TimeZone.getTimeZone("UTC")
            return timeFormat.format(date)
        }

        fun getDayMonthFromSeconds(seconds: Long): String {
            val date=Date(seconds*1000)
            val timeFormat=SimpleDateFormat("dd/MM", Locale.ENGLISH)
            timeFormat.timeZone= TimeZone.getTimeZone("UTC")
            return timeFormat.format(date)


        }
        fun getDayFromSeconds(seconds: Long): String {
            val date = Date(seconds * 1000)
            val timeFormat = SimpleDateFormat("EEE", Locale.ENGLISH)
            timeFormat.timeZone = TimeZone.getTimeZone("UTC")


            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            val today = calendar.get(Calendar.DAY_OF_YEAR)
            val year = calendar.get(Calendar.YEAR)

            calendar.time = date
            val givenDay = calendar.get(Calendar.DAY_OF_YEAR)
            val givenYear = calendar.get(Calendar.YEAR)
            return if (givenDay == today && givenYear == year) "Today" else timeFormat.format(date)
        }
        fun isNightTime(currentTime: Long, sunrise: Long, sunset: Long): Boolean {
            return currentTime < sunrise || currentTime > sunset
        }
        fun hasDurationPassed(timeSeconds: Long, days: Int = 0, hours: Int = 0, minutes: Int = 0): Boolean {
            val currentUtcTime = System.currentTimeMillis() / 1000
            val timeDifference = (days * 24 * 3600) + (hours * 3600) + (minutes * 60)
            return currentUtcTime >= (timeSeconds + timeDifference)
        }

        fun hasTimePassed(seconds: Long): Boolean {
            val currentUtcTime = System.currentTimeMillis() / 1000
            return currentUtcTime>=seconds
        }
        fun getLocalDateTimeFromSeconds(seconds: Long): String {

            val date = Date(seconds * 1000)

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getDefault()

            return dateFormat.format(date)
        }
    }

}