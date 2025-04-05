package com.example.weatherforcast.placePicker.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherforcast.R
import com.example.weatherforcast.placePicker.PlacesViewModel

@Composable
fun SearchPlaceBar(viewModel: PlacesViewModel,
                   onQueryChange: (String) -> Unit,
                   onPlaceSelected: /*(LatLng, */(String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val predictions by viewModel.predictions.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                onQueryChange(it)
            },
            label = { Text(stringResource(R.string.search_location)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        )

        LazyColumn {
            items(predictions) { prediction ->
                Text(text = prediction.getPrimaryText(null).toString(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onPlaceSelected(prediction.placeId)
                        }
                        .padding(16.dp)
                )
            }
        }

    }

}