package com.example.weatherforcast.homeScreen.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherforcast.R
import com.example.weatherforcast.model.data.WeatherInfo

@Composable
fun GlowingWeatherCircle(sizeDP: Dp =300.dp, weatherInfo: WeatherInfo) {
    Box(
        modifier = Modifier
            .size(sizeDP)
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Transparent,
                        Color.Transparent,
                        Color.Transparent,
                        Color.Transparent,
                        Color.White.copy(alpha = 0.4f),
                        Color.White.copy(alpha = 0.5f),
                        Color.White.copy(alpha = 0.6f)
                    ),
                    radius = radius * 1.5f
                ),
                radius = radius
            )

        }
        Column(
            modifier = Modifier
                .size(sizeDP * 0.9f)
                .background(Color.Transparent),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.feels_like))
                    withStyle(style = SpanStyle(fontSize = 14.sp)) {
                        append("®")

                    }
                    append(weatherInfo.feelsLike.toString())
                },
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold

            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = buildAnnotatedString {
                    append(weatherInfo.temp.toString())
                    withStyle(
                        style = SpanStyle(
                            fontSize = 16.sp,
                            baselineShift = BaselineShift.Superscript,
                        )
                    ) {
                        append("o")
                        withStyle(
                            style = SpanStyle(
                                fontSize = 16.sp,
                                baselineShift = BaselineShift.Subscript
                            )
                        ) {
                            append(weatherInfo.temperatureUnit.unitSymbol)

                        }
                    }
                },
                color = Color.White,
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,

                )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Image(
                        painter = painterResource(R.drawable.ic_min_temp),
                        modifier = Modifier
                            .size(15.dp)
                            .align(Alignment.CenterVertically),
                        contentDescription = stringResource(R.string.lowest_temperature)
                    )
                    Text(
                        text = buildAnnotatedString {
                            append(weatherInfo.minTemp.toString())
                            withStyle(
                                style = SpanStyle(
                                    fontSize = 8.sp,
                                    baselineShift = BaselineShift.Superscript,
                                )
                            ) {
                                append("o")
                                withStyle(
                                    style = SpanStyle(
                                        fontSize = 8.sp,
                                        baselineShift = BaselineShift.Subscript
                                    )
                                ) {
                                    append(weatherInfo.temperatureUnit.unitSymbol)

                                }
                            }
                        },
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,

                        )
                }
                Text(
                    text = weatherInfo.weatherDescription,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
                Row {
                    Text(
                        text = buildAnnotatedString {
                            append(weatherInfo.maxTemp.toString())
                            withStyle(
                                style = SpanStyle(
                                    fontSize = 8.sp,
                                    baselineShift = BaselineShift.Superscript,
                                )
                            ) {
                                append("o")
                                withStyle(
                                    style = SpanStyle(
                                        fontSize = 8.sp,
                                        baselineShift = BaselineShift.Subscript
                                    )
                                ) {
                                    append(weatherInfo.temperatureUnit.unitSymbol)

                                }
                            }
                        },
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Image(
                        painter = painterResource(R.drawable.ic_max_temp),
                        modifier = Modifier
                            .size(15.dp)
                            .align(Alignment.CenterVertically),
                        contentDescription = stringResource(R.string.highest_temperature)
                    )

                }

            }
            Spacer(Modifier.height(10.dp))
            Row(modifier = Modifier.padding(horizontal = 30.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.5f))
                        .fillMaxWidth()
                ) {
                    Row {
                        Image(
                            painter = painterResource(R.drawable.ic_humidity),
                            modifier = Modifier
                                .size(15.dp)
                                .align(Alignment.CenterVertically),
                            contentDescription = stringResource(R.string.humidity)
                        )
                        Text(
                            text = buildAnnotatedString {
                                append(weatherInfo.humidityPercentage.toString())
                                append("%")
                            },
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Row {
                        Image(
                            painter = painterResource(R.drawable.ic_wind_speed),
                            modifier = Modifier
                                .size(15.dp)
                                .align(Alignment.CenterVertically),
                            contentDescription = stringResource(R.string.wind_speed)
                        )
                        Text(
                            text = buildAnnotatedString {
                                append(weatherInfo.windSpeed.toString())
                                append(weatherInfo.windSpeedUnit.unitSymbol)
                            },
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Row {
                        Image(
                            painter = painterResource(R.drawable.ic_cloud),
                            modifier = Modifier
                                .size(15.dp)
                                .align(Alignment.CenterVertically),
                            contentDescription = stringResource(R.string.cloud_percentage)
                        )
                        Text(
                            text = buildAnnotatedString {
                                append(weatherInfo.cloudyPercentage.toString())
                                append("%")
                            },
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
            Image(
                painter = painterResource(weatherInfo.iconInfo),
                contentDescription = stringResource(R.string.weather_image),
                Modifier.size(150.dp)
            )

        }


    }

}