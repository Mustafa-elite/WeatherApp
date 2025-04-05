package com.example.weatherforcast.settings.ui

import android.app.Activity
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.recreate
import com.example.weatherforcast.R
import com.example.weatherforcast.helpyclasses.AppLang
import com.example.weatherforcast.helpyclasses.LanguageUtil
import com.example.weatherforcast.model.data.TemperatureUnit
import com.example.weatherforcast.model.data.WindSpeedUnit
import com.example.weatherforcast.settings.SettingViewModel
import com.yariksoffice.lingver.Lingver

@Composable
fun SettingScreen(settingViewModel: SettingViewModel) {
    val language by settingViewModel.language.collectAsState()
    val temperatureUnit by settingViewModel.temperatureUnit.collectAsState()
    val speedUnit by settingViewModel.speedUnit.collectAsState()

    val context= LocalContext.current
    val activity= LocalActivity.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = stringResource(R.string.language), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        MyDropdownMenu(
            selectedOption = language,
            options = AppLang.entries.map { it.name},
            onOptionSelected = {
                //Lingver.getInstance().setLocale(context,language)
                activity?.let { it1 -> LanguageUtil.changeLanguage(it1,AppLang.valueOf(it)) }
                settingViewModel.setLanguage(it)
                //Log.i("TAG", "SettingScreen: language changed")
               // activity?.let { it1 -> recreate(it1) }
            }
        )

        Text(text = stringResource(R.string.temperature_unit), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        MyDropdownMenu(
            selectedOption = temperatureUnit.name,
            options = TemperatureUnit.entries.map { it.name },
            onOptionSelected = { settingViewModel.setTemperatureUnit(TemperatureUnit.valueOf(it)) }
        )

        Text(text = stringResource(R.string.speed_unit), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        MyDropdownMenu(
            selectedOption = speedUnit.unitSymbol,
            options = WindSpeedUnit.entries.map { it.unitSymbol},
            onOptionSelected = {selectedName->
                val windSpeedUnit=WindSpeedUnit.entries.find { it.unitSymbol==selectedName }?: WindSpeedUnit.KILOMETER_HOUR
                settingViewModel.setSpeedUnit(windSpeedUnit) }
        )
    }
}

@Composable
fun MyDropdownMenu(
    selectedOption: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box (modifier = Modifier.fillMaxWidth()){
        Text(
            text = selectedOption,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .border(1.dp, Color.Gray)
                .padding(8.dp)
        )
        DropdownMenu(modifier = Modifier.fillMaxWidth(),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}