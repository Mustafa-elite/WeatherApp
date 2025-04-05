package com.example.weatherforcast.homeScreen.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.example.weatherforcast.R
import com.example.weatherforcast.helpyclasses.DateManager
import com.example.weatherforcast.model.data.ThreeHoursWeatherData
import kotlin.math.round

@Composable
fun HourlyForecast(threeHoursItem: ThreeHoursWeatherData, timezone: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = DateManager.getTimeFromSeconds(threeHoursItem.dt , timezone),
            color = Color.White
        )
        Image(
            painter = painterResource(threeHoursItem.weather[0].iconRes),
            contentDescription = stringResource(R.string.weather_image),
            Modifier.fillMaxWidth()
        )
        Text(buildAnnotatedString {
            append(round(threeHoursItem.main.temp).toInt().toString())
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