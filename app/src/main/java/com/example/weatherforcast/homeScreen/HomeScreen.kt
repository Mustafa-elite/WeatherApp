package com.example.weatherforcast.homeScreen

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherforcast.R
import com.example.weatherforcast.helpyclasses.DateManager
import com.example.weatherforcast.model.data.DailyWeatherData
import com.example.weatherforcast.model.data.Response
import com.example.weatherforcast.model.data.TemperatureUnit
import com.example.weatherforcast.model.data.ThreeHoursWeatherData
import com.example.weatherforcast.model.data.WeatherInfo
import com.example.weatherforcast.model.data.WindSpeedUnit
import kotlin.math.round

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(homeViewModel: HomeViewModel){
    val weatherState by homeViewModel.weatherResponse.collectAsState()
    LaunchedEffect(key1 = true) {


        homeViewModel.getRecentWeather(31.2357, 30.0444)
        //homeViewModel.getRecentWeather(31.485828,30.002523)
    }


    Box (modifier = Modifier
        .fillMaxSize()
        .background(colorResource(R.color.test_color)),
        contentAlignment = Alignment.Center
    ){
        Column(modifier = Modifier
            .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally) {
            when(weatherState){
                is Response.Failure ->
                {}
                is Response.Loading -> {

                }
                is Response.Success -> WeatherDetailsScreen(weatherInfo = (weatherState as Response.Success).data)
            }
            //Text(text = weatherState?.main?.temp.toString())
        }


    }
}
@Preview(showSystemUi = true)
@Composable
fun WeatherDetailsScreen(weatherInfo: WeatherInfo= WeatherInfo()){
    Box(modifier = Modifier.fillMaxSize()){
        Image(
            painter = painterResource(weatherInfo.getBackgroundImageRes()),
            contentDescription = "background image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
        Column (modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally){
            Row (Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween){
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = stringResource(R.string.today), fontSize = 20.sp, color = Color.White)
                    Text(text =DateManager.SecondsToWrittenDate(
                        (System.currentTimeMillis()/1000)+weatherInfo.timezone),
                        color = Color.White)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = weatherInfo.cityName, fontSize = 20.sp, color = Color.White)
                    Text(text = weatherInfo.countryName, color = Color.White)
                }
            }
            Spacer(Modifier.height(20.dp))
            TransparentCircle(weatherInfo = weatherInfo)
            Spacer(Modifier.height(20.dp))
            Row (Modifier.fillMaxWidth().background(Color.Black.copy(alpha = 0.3f)),horizontalArrangement = Arrangement.SpaceAround){
                Text(buildAnnotatedString {
                    append(stringResource(R.string.sunrise))
                    append(DateManager.getTimeFromSeconds(
                        weatherInfo.sunrise+weatherInfo.timezone))
                }, color = Color.White)
                Text(buildAnnotatedString {
                    append(stringResource(R.string.sunset))
                    append(DateManager.getTimeFromSeconds(
                        weatherInfo.sunset+weatherInfo.timezone))
                }, color = Color.White)
            }
            Spacer(Modifier.height(20.dp))
            Column (Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.3f))
                .padding(5.dp)) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(painter = painterResource(R.drawable.clock), contentDescription = "Hourly Forecast ")
                    Text(stringResource(R.string._3_hours_gap_forecast), color = Color.White)


                }
                Spacer(Modifier.height(10.dp))
                LazyRow (modifier = Modifier.padding(5.dp), horizontalArrangement = Arrangement.spacedBy(30.dp)){
                    items(items =weatherInfo.threeHoursForecast ){threeHoursItem->
                        HourlyForecast(threeHoursItem,weatherInfo.timezone)
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            Column (Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.3f))
                .padding(5.dp),
                verticalArrangement = Arrangement.spacedBy(30.dp)) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon( imageVector = Icons.Default.DateRange, contentDescription = "Hourly Forecast ", tint = Color.White)
                    Text(stringResource(R.string.daily_forecast), color = Color.White)


                }

                weatherInfo.daysForecast.forEach{
                        dayItem-> DailyForecastItem(dayItem,weatherInfo.timezone)
                }
            }

        }

    }

}

@Composable
fun DailyForecastItem(dayItem: DailyWeatherData, timezone: Int) {
    Row (modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween){
        Row {
            Column (horizontalAlignment = Alignment.CenterHorizontally){
                Text(text =DateManager.getDayMonthFromSeconds(dayItem.dt+timezone), color = Color.White)
                Text(DateManager.getDayFromSeconds(dayItem.dt+timezone), fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(Modifier.width(20.dp))
            Image(painter = painterResource(dayItem.weather[0].iconRes), contentDescription = "Weather State")

        }
        Text(text = dayItem.weather[0].description, color = Color.White)

        Text(buildAnnotatedString {
            append(round(dayItem.temp.min).toInt().toString())
            withStyle(style = SpanStyle(fontSize = 8.sp, baselineShift = BaselineShift.Superscript)){
                append("o")
            }
            append("/")
            append(round(dayItem.temp.max).toInt().toString())
            withStyle(style = SpanStyle(fontSize = 8.sp, baselineShift = BaselineShift.Superscript)){
                append("o")
            }
        }, color = Color.White)

    }

}

@Composable
fun HourlyForecast(threeHoursItem: ThreeHoursWeatherData, timezone: Int) {
    Column (horizontalAlignment = Alignment.CenterHorizontally){
        Text(text=DateManager.getTimeFromSeconds(threeHoursItem.dt+timezone), color = Color.White)
        Image(painter = painterResource(threeHoursItem.weather[0].iconRes),
            contentDescription = "weather image",
            Modifier.fillMaxWidth())
        Text(buildAnnotatedString {
            append(threeHoursItem.main.temp.toString())
            withStyle(style = SpanStyle(fontSize = 8.sp, baselineShift = BaselineShift.Superscript)){
                append("o")
            }
        }, color = Color.White)
    }
}

@Composable
fun TransparentCircle(sizeDP: Dp=300.dp,weatherInfo: WeatherInfo) {
    Box(modifier = Modifier
        .size(sizeDP)
        .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius=size.minDimension/2
            drawCircle(
                brush = Brush.radialGradient(
                    colors =listOf(
                        Color.Transparent,
                        Color.Transparent,
                        Color.Transparent,
                        Color.Transparent,
                        Color.Transparent,
                        Color.White.copy(alpha = 0.4f),
                        Color.White.copy(alpha = 0.5f),
                        Color.White.copy(alpha = 0.6f)
                    ),
                    radius = radius *1.5f
                ),
                radius = radius
            )

        }
        Column (modifier = Modifier
            .size(sizeDP * 0.9f)
            .background(Color.Transparent),
            horizontalAlignment = Alignment.CenterHorizontally){
            Spacer(Modifier.height(10.dp))
            Text(
                text =buildAnnotatedString {
                    append("Feels Like ")
                    withStyle(style = SpanStyle(fontSize = 14.sp)) {
                        append("®")

                    }
                    append(weatherInfo.feelsLike.toString() )
                },
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold

            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = buildAnnotatedString {
                    append(weatherInfo.temp.toString())
                    withStyle(style = SpanStyle(fontSize = 16.sp, baselineShift = BaselineShift.Superscript,)){
                        append("o")
                        withStyle(style = SpanStyle(fontSize = 16.sp, baselineShift = BaselineShift.Subscript)){
                            append(weatherInfo.temperatureUnit.unitSymbol)

                        }
                    }
                },
                color = Color.White,
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,

            )
            Spacer(Modifier.height(10.dp))
            Row (modifier = Modifier.fillMaxWidth(),horizontalArrangement =Arrangement.SpaceBetween){
                Row {
                    Image(painter = painterResource(R.drawable.ic_min_temp),
                        modifier =Modifier
                            .size(15.dp)
                            .align(Alignment.CenterVertically),
                        contentDescription = "Lowest Temperature")
                    Text(
                        text = buildAnnotatedString {
                            append(weatherInfo.minTemp.toString())
                            withStyle(style = SpanStyle(fontSize = 8.sp, baselineShift = BaselineShift.Superscript,)){
                                append("o")
                                withStyle(style = SpanStyle(fontSize = 8.sp, baselineShift = BaselineShift.Subscript)){
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
                            withStyle(style = SpanStyle(fontSize = 8.sp, baselineShift = BaselineShift.Superscript,)){
                                append("o")
                                withStyle(style = SpanStyle(fontSize = 8.sp, baselineShift = BaselineShift.Subscript)){
                                    append(weatherInfo.temperatureUnit.unitSymbol)

                                }
                            }
                        },
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Image(painter = painterResource(R.drawable.ic_max_temp),
                        modifier =Modifier
                            .size(15.dp)
                            .align(Alignment.CenterVertically),
                        contentDescription = "highest Temperature")

                }

            }
            Spacer(Modifier.height(10.dp))
            Row (modifier = Modifier.padding(horizontal = 30.dp)){
                Row (horizontalArrangement =Arrangement.SpaceBetween,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.5f))
                        .fillMaxWidth()) {
                    Row {
                        Image(painter = painterResource(R.drawable.ic_humidity),
                            modifier =Modifier
                                .size(15.dp)
                                .align(Alignment.CenterVertically),
                            contentDescription = "Humedity")
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
                        Image(painter = painterResource(R.drawable.ic_wind_speed),
                            modifier =Modifier
                                .size(15.dp)
                                .align(Alignment.CenterVertically),
                            contentDescription = "wind speed")
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
                        Image(painter = painterResource(R.drawable.ic_cloud),
                            modifier =Modifier
                                .size(15.dp)
                                .align(Alignment.CenterVertically),
                            contentDescription = "cloud percentage")
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
            Image(painter = painterResource(weatherInfo.iconInfo),
                contentDescription = "wheather image",
                Modifier.size(150.dp))

        }




    }

}
