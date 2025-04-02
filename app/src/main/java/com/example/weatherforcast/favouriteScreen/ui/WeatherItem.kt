package com.example.weatherforcast.favouriteScreen.ui

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherforcast.model.data.WeatherInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherItem(
    weatherInfo: WeatherInfo,
    isDefault:Boolean,
    onDelete:(WeatherInfo)->Unit,
    onSetDefault:(WeatherInfo)->Unit,
    onClick: (WeatherInfo) -> Unit
) {
    val dismissState= rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart || dismissValue == SwipeToDismissBoxValue.StartToEnd) {
                onDelete(weatherInfo)
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    //.background(Color.Red)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        content = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onClick(weatherInfo) },
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    Image(
                        painter = painterResource(id = weatherInfo.getBackgroundImageRes()),
                        contentDescription = "Weather Background",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Column(
                            modifier = Modifier
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
                                painter = painterResource(weatherInfo.iconInfo),
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
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(verticalArrangement = Arrangement.SpaceBetween) {
                                Icon(
                                    modifier = Modifier
                                        .clickable { onDelete(weatherInfo) },
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Delete",
                                    tint = Color.Red
                                )
                                Spacer(Modifier.height(10.dp))
                                // Arrow Icon
                                Icon(
                                    modifier = Modifier.clickable { onSetDefault(weatherInfo) },
                                    imageVector = if (isDefault) Icons.Default.Star else Icons.Outlined.Star,
                                    contentDescription = "Set as Default",
                                    tint = if (isDefault) Color.Yellow else Color.White
                                )
                            }
                        }
                    }
                }

            }
        }
    )


}