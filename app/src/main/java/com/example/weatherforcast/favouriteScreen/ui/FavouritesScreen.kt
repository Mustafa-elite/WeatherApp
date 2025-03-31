package com.example.weatherforcast.favouriteScreen.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherforcast.favouriteScreen.FavViewResponse
import com.example.weatherforcast.favouriteScreen.FavouritesViewModel
import com.example.weatherforcast.model.data.WeatherInfo

@Composable
fun FavouritesScreen(
    favViewModel: FavouritesViewModel,
    navigateToMapAction:()->Unit,
    favItemClick: (WeatherInfo) -> Unit){
    val favWeatherResponse by favViewModel.favWeatherResponse.collectAsState()

    LaunchedEffect(Unit) {
        favViewModel.getFavData()
    }
    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        FloatingActionButton(
            onClick = { navigateToMapAction() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "add to Favorite")
        }

        when(favWeatherResponse){
            is FavViewResponse.Failure -> TODO()
            is FavViewResponse.Loading -> LoadingIndicator()
            is FavViewResponse.Success -> FavList((favWeatherResponse as FavViewResponse.Success).dataList,favItemClick)
        }



    }
}

@Composable
fun FavList(dataList: List<WeatherInfo>, favItemClick: (WeatherInfo) -> Unit) {
    LazyColumn {
        items(dataList){
            WeatherItem(it,favItemClick)
        }
    }
}

@Composable
fun WeatherItem(
    weatherInfo: WeatherInfo,
    onClick: (WeatherInfo) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick(weatherInfo) },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box( modifier = Modifier
            .fillMaxWidth()){
            Image(
                painter = painterResource(id = weatherInfo.getBackgroundImageRes()),
                contentDescription = "Weather Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = weatherInfo.countryName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = weatherInfo.cityName,
                        fontSize = 14.sp,
                        color = Color.LightGray
                    )
                }

                // Temperature & Weather
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(weatherInfo.iconInfo) ,
                        contentDescription = "Weather Icon",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = weatherInfo.temp.toString(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = weatherInfo.weatherDescription,
                            fontSize = 14.sp,
                            color = Color.LightGray
                        )
                    }
                }

                // Arrow Icon
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Go",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

        }

    }
}


@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize(), contentAlignment = Alignment.Center
    ){
        CircularProgressIndicator()
    }
}