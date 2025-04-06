package com.example.weatherforcast.homeScreen.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherforcast.R
import com.example.weatherforcast.helpyclasses.DateManager
import com.example.weatherforcast.model.data.WeatherInfo

@Preview(showSystemUi = true)
@Composable
fun WeatherDetailsScreen(
    weatherInfo: WeatherInfo = WeatherInfo(),
    navigateToHome: () -> Unit = {},
    navigateToMap: () -> Unit={}){
    var showDialog by remember { mutableStateOf(false) }
    //var selectedOption by remember { mutableStateOf<String?>(null) }
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(weatherInfo.getBackgroundImageRes()),
            contentDescription = stringResource(R.string.background_image),
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.today),
                        fontSize = 20.sp,
                        color = Color.White
                    )
                    Text(
                        text = DateManager.secondsToWrittenDate(
                            (System.currentTimeMillis() / 1000) , weatherInfo.timezone
                        ),
                        color = Color.White
                    )
                }
                Column( horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = DateManager.getCurrentTime(weatherInfo.timezone),
                        fontSize = 20.sp,
                        color = Color.White
                    )
                    Text(
                        text = stringResource(R.string.change_location),
                        modifier = Modifier.clickable { showDialog=true },
                        fontSize = 16.sp,
                        color = Color.White
                    )


                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = weatherInfo.cityName, fontSize = 18.sp, color = Color.White)
                    Text(text = weatherInfo.countryName, color = Color.White)
                }
            }
            Spacer(Modifier.height(20.dp))
            GlowingWeatherCircle(weatherInfo = weatherInfo)
            Spacer(Modifier.height(20.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.3f)),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(buildAnnotatedString {
                    append(stringResource(R.string.sunrise))
                    append(
                        DateManager.getTimeFromSeconds(
                            weatherInfo.sunrise , weatherInfo.timezone
                        )
                    )
                }, color = Color.White)
                Text(buildAnnotatedString {
                    append(stringResource(R.string.sunset))
                    append(
                        DateManager.getTimeFromSeconds(
                            weatherInfo.sunset , weatherInfo.timezone)
                    )
                }, color = Color.White)
            }
            Spacer(Modifier.height(20.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .padding(5.dp)
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.clock),
                        contentDescription = stringResource(R.string.hourly_forecast)
                    )
                    Text(stringResource(R.string._3_hours_gap_forecast), color = Color.White)


                }
                Spacer(Modifier.height(10.dp))
                LazyRow(
                    modifier = Modifier.padding(5.dp),
                    horizontalArrangement = Arrangement.spacedBy(30.dp)
                ) {
                    items(items = weatherInfo.threeHoursForecast.take(8)) { threeHoursItem ->
                        HourlyForecast(threeHoursItem, weatherInfo.timezone)
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .padding(5.dp),
                verticalArrangement = Arrangement.spacedBy(30.dp)
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = stringResource(R.string.hourly_forecast),
                        tint = Color.White
                    )
                    Text(stringResource(R.string.daily_forecast), color = Color.White)


                }

                weatherInfo.daysForecast.take(7).forEach { dayItem ->
                    DailyForecastItem(dayItem, weatherInfo.timezone)
                }
            }


            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = {
                        Text(
                            text = "Change Your Desired Location",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Please Select How would you like to change the Location",
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Button(
                                    onClick = {
                                        showDialog = false
                                        navigateToHome.invoke()
                                        //selectedOption = "GPS"

                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = colorResource(id = R.color.Default_Colour2),
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("GPS")
                                }


                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = {
                                        showDialog = false
                                        navigateToMap.invoke()
                                        //selectedOption = "PlacePicker"

                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = colorResource(id = R.color.Default_Colour2),
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("PlacePicker")
                                }
                            }
                        }
                    },
                    confirmButton = {},
                    dismissButton = {}
                )
            }


        }

    }

}