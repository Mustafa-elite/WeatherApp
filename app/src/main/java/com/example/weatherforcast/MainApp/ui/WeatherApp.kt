package com.example.weatherforcast.MainApp.ui

import android.content.Context
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.weatherforcast.AlarmScreen.AlertMakerViewModelFactory
import com.example.weatherforcast.AlarmScreen.AlertViewModelFactory
import com.example.weatherforcast.AlarmScreen.ui.AlertMakerScreen
import com.example.weatherforcast.AlarmScreen.ui.AlertsScreen
import com.example.weatherforcast.favouriteScreen.FavouritesViewModel
import com.example.weatherforcast.favouriteScreen.FavouritesViewModelFactory
import com.example.weatherforcast.favouriteScreen.ui.FavouritesScreen
import com.example.weatherforcast.homeScreen.HomeViewModel
import com.example.weatherforcast.homeScreen.HomeViewModelFactory
import com.example.weatherforcast.homeScreen.ui.HomeScreen
import com.example.weatherforcast.homeScreen.ui.WeatherDetailsScreen
import com.example.weatherforcast.model.data.WeatherInfo
import com.example.weatherforcast.model.local.SharedPrefImp
import com.example.weatherforcast.model.local.WeatherDatabase
import com.example.weatherforcast.model.local.WeatherLocalDatSource
import com.example.weatherforcast.model.remote.RetrofitHelper
import com.example.weatherforcast.model.remote.WeatherRemoteDataSource
import com.example.weatherforcast.model.repositories.WeatherDataRepository
import com.example.weatherforcast.placePicker.PlacesViewModelFactory
import com.example.weatherforcast.placePicker.ui.PlacePicker
import com.example.weatherforcast.settings.SettingViewModelFactory
import com.example.weatherforcast.settings.ui.SettingScreen
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.gson.Gson

@Composable
fun WeatherApp() {
    var navController = rememberNavController()
    var getWeatherAction: ((LatLng) -> Unit)? = null
    var removeDefaultWeatherAction:(()->Unit)?=null
    val snackbarHostState = remember { SnackbarHostState() }
    val placesClient= Places.createClient(LocalContext.current)
    val currentRoute = remember { mutableStateOf(Screen.Splash.rout) }
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect {
            currentRoute.value = it.destination.route ?: Screen.Home.rout
        }
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = {
            //val currentRout=navController.currentBackStackEntry?.destination?.route
            if (currentRoute.value != Screen.Splash.rout) {
                BottomNavigationBar(navController)
            }
        }, snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.rout,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = Screen.Splash.rout) {
                MySplashScreen {
                    navController.navigate(Screen.Home.rout) {
                        popUpTo(Screen.Splash.rout) { inclusive = true }
                    }
                }
            }
            composable(route = Screen.Home.rout) {
                val viewmodel: HomeViewModel = viewModel(
                    factory = HomeViewModelFactory(
                        WeatherDataRepository.getInstance(
                            WeatherRemoteDataSource(RetrofitHelper.weatherService),
                            WeatherLocalDatSource(
                                WeatherDatabase.getInstance(LocalContext.current).getWeatherDao(),
                                SharedPrefImp(
                                    LocalContext.current.getSharedPreferences(
                                        "weather_prefs",
                                        Context.MODE_PRIVATE
                                    )
                                )
                            )
                        )
                    )
                )
                getWeatherAction = { latlon ->
                    viewmodel.getRecentWeather(latlon.longitude, latlon.latitude)
                    Log.i("TAG", "WeatherApp: setting action")
                }
                removeDefaultWeatherAction={viewmodel.removeDefaultLocation() }
                HomeScreen(
                    viewmodel,
                    snackbarHostState,
                    { navController.navigate(Screen.Map.rout) },
                    {
                        removeDefaultWeatherAction?.invoke()
                        navController.navigate(Screen.Home.rout)
                    }
                )
            }
            composable(route = Screen.Favourites.rout) {
                val viewModel: FavouritesViewModel = viewModel(
                    factory = FavouritesViewModelFactory(
                        WeatherDataRepository.getInstance(
                            WeatherRemoteDataSource(RetrofitHelper.weatherService),
                            WeatherLocalDatSource(
                                WeatherDatabase.getInstance(LocalContext.current).getWeatherDao(),
                                SharedPrefImp(
                                    LocalContext.current.getSharedPreferences(
                                        "weather_prefs",
                                        Context.MODE_PRIVATE
                                    )
                                )
                            )
                        )
                    )
                )
                getWeatherAction =
                    { latLng -> viewModel.addFavWeather(latLng.longitude, latLng.latitude) }
                FavouritesScreen(
                    viewModel,
                    {
                        navController.navigate(Screen.Map.rout)
                    },
                    { weatherInfo ->
                        val weatherInfoJson = Gson().toJson(weatherInfo)
                        navController.navigate(Screen.Details.createRoute(weatherInfoJson))
                    }
                )
            }
            composable(route = Screen.Alerts.rout) {
                AlertsScreen(
                    viewModel(
                        factory = AlertViewModelFactory(
                            WeatherDataRepository.getInstance(
                                WeatherRemoteDataSource(RetrofitHelper.weatherService),
                                WeatherLocalDatSource(
                                    WeatherDatabase.getInstance(LocalContext.current)
                                        .getWeatherDao(),
                                    SharedPrefImp(
                                        LocalContext.current.getSharedPreferences(
                                            "weather_prefs",
                                            Context.MODE_PRIVATE
                                        )
                                    )
                                )
                            )
                        )
                    ), { navController.navigate(Screen.AlertMaker.rout) })
            }
            composable(route = Screen.Setting.rout) {
                SettingScreen(
                    viewModel(
                        factory = SettingViewModelFactory(
                            WeatherDataRepository.getInstance(
                                WeatherRemoteDataSource(RetrofitHelper.weatherService),
                                WeatherLocalDatSource(
                                    WeatherDatabase.getInstance(LocalContext.current)
                                        .getWeatherDao(),
                                    SharedPrefImp(
                                        LocalContext.current.getSharedPreferences(
                                            "weather_prefs",
                                            Context.MODE_PRIVATE
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            }
            composable(route = Screen.Map.rout) {
                PlacePicker(
                    viewModel(
                        factory = PlacesViewModelFactory(
                            WeatherDataRepository.getInstance(
                                WeatherRemoteDataSource(RetrofitHelper.weatherService),
                                WeatherLocalDatSource(
                                    WeatherDatabase.getInstance(LocalContext.current)
                                        .getWeatherDao(),
                                    SharedPrefImp(
                                        LocalContext.current.getSharedPreferences(
                                            "weather_prefs",
                                            Context.MODE_PRIVATE
                                        )
                                    )
                                )
                            ),
                            placesClient
                        )
                    ),
                    { latlon ->
                        getWeatherAction?.invoke(latlon)
                        navController.popBackStack()

                    }
                )
            }
            composable(
                route = Screen.Details.rout,
                arguments = listOf(navArgument("weatherInfo") { type = NavType.StringType })
            ) { backStackEntry ->
                val json = backStackEntry.arguments?.getString("weatherInfo")
                val weatherInfo = Gson().fromJson(json, WeatherInfo::class.java)
                weatherInfo?.let {
                    WeatherDetailsScreen(it,
                        {
                            removeDefaultWeatherAction?.invoke()
                            navController.navigate(Screen.Home.rout)
                        },
                        {navController.navigate(Screen.Map.rout)})
                }
            }
            composable(route = Screen.AlertMaker.rout) {
                AlertMakerScreen(viewModel(
                    factory = AlertMakerViewModelFactory(
                        WeatherDataRepository.getInstance(
                            WeatherRemoteDataSource(RetrofitHelper.weatherService),
                            WeatherLocalDatSource(
                                WeatherDatabase.getInstance(LocalContext.current).getWeatherDao(),
                                SharedPrefImp(
                                    LocalContext.current.getSharedPreferences(
                                        "weather_prefs",
                                        Context.MODE_PRIVATE
                                    )
                                )
                            )
                        )
                    )
                ), { navController.popBackStack() })
            }
        }
        BackHandler(
            enabled = currentRoute.value == Screen.Favourites.rout ||
                    currentRoute.value == Screen.Alerts.rout ||
                    currentRoute.value == Screen.Setting.rout
        ) {

            navController.navigate(Screen.Home.rout) {
                popUpTo(Screen.Home.rout) {
                    inclusive = false
                }
            }
        }


    }
}