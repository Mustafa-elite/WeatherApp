package com.example.weatherforcast.helpyclasses

import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.time.Instant
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

    }

}