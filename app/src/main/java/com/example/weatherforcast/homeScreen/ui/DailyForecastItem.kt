package com.example.weatherforcast.homeScreen.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherforcast.MainApp.getLocaleString
import com.example.weatherforcast.MainApp.getLocaleWeatherDescription
import com.example.weatherforcast.R
import com.example.weatherforcast.helpyclasses.DateManager
import com.example.weatherforcast.model.data.DailyWeatherData
import kotlin.math.round

@Composable
fun DailyForecastItem(dayItem: DailyWeatherData, timezone: Int) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Row {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = DateManager.getDayMonthFromSeconds(dayItem.dt , timezone),
                    color = Color.White
                )
                Text(
                    DateManager.getDayFromSeconds(dayItem.dt , timezone),
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(Modifier.width(20.dp))
            Image(
                painter = painterResource(dayItem.weather[0].iconRes),
                contentDescription = stringResource(R.string.weather_state)
            )

        }
        Text(text = dayItem.weather[0].description.getLocaleWeatherDescription(), color = Color.White)

        Text(buildAnnotatedString {
            append(round(dayItem.temp.min).toInt().getLocaleString())
            withStyle(
                style = SpanStyle(
                    fontSize = 8.sp,
                    baselineShift = BaselineShift.Superscript
                )
            ) {
                append("o")
            }
            append("/")
            append(round(dayItem.temp.max).toInt().getLocaleString())
            withStyle(
                style = SpanStyle(
                    fontSize = 8.sp,
                    baselineShift = BaselineShift.Superscript
                )
            ) {
                append("o")
            }
        }, color = Color.White)

    }

}