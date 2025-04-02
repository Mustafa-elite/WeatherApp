package com.example.weatherforcast

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.weatherforcast.favouriteScreen.FavouritesViewModel
import com.example.weatherforcast.favouriteScreen.FavouritesViewModelFactory
import com.example.weatherforcast.favouriteScreen.ui.FavouritesScreen
import com.example.weatherforcast.homeScreen.HomeViewModel
import com.example.weatherforcast.homeScreen.ui.HomeScreen
import com.example.weatherforcast.homeScreen.HomeViewModelFactory
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
import com.example.weatherforcast.ui.theme.WeatherForcastTheme
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.gson.Gson
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (!Places.isInitialized()) {
            Places.initialize(this, "AIzaSyACknaVvXwj6TEcJeADVCmPxmaJ_qY7HD8")
        }
        setContent {
            WeatherForcastTheme {
                WeatherApp()
            }
        }
    }

}

@Composable
fun MySplashScreen(action: () -> Unit) {
    val alpha = remember {
        Animatable(0f)
    }
    LaunchedEffect(key1 = true) {
        alpha.animateTo(
            1f,
            animationSpec = tween(1500)
        )
        delay(2000)
        action()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.Default_Colour)),
        contentAlignment = Alignment.Center
    ) {
        Image(painter = painterResource(R.drawable.splash), contentDescription = "Splash Screen")
    }
}

@Composable
private fun WeatherApp() {
    var navController = rememberNavController()
    var action: ((LatLng) -> Unit)? = null
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
        }
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
                val viewmodel:HomeViewModel =viewModel(
                    factory = HomeViewModelFactory(
                        WeatherDataRepository.getInstance(
                            WeatherRemoteDataSource(RetrofitHelper.weatherService),
                            WeatherLocalDatSource(
                                WeatherDatabase.getInstance(LocalContext.current).getWeatherDao(),
                                SharedPrefImp(LocalContext.current.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE))
                            )
                        )
                    )
                )
                HomeScreen(viewmodel,
                {
                    action={latlon->viewmodel.getRecentWeather(latlon.longitude,latlon.latitude)}
                    navController.navigate(Screen.Map.rout)
                })
            }
            composable(route = Screen.Favourites.rout) {
                val viewModel : FavouritesViewModel = viewModel(
                    factory = FavouritesViewModelFactory(
                        WeatherDataRepository.getInstance(
                            WeatherRemoteDataSource(RetrofitHelper.weatherService),
                            WeatherLocalDatSource(
                                WeatherDatabase.getInstance(LocalContext.current).getWeatherDao(),
                                SharedPrefImp(LocalContext.current.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE))
                            )
                        )
                    )
                )
                FavouritesScreen(
                    viewModel,
                    //sending the navigation to the map as a lamda
                    {
                        action ={latLng->viewModel.addFavWeather(latLng.longitude,latLng.latitude)}
                        navController.navigate(Screen.Map.rout)
                    },
                    { weatherInfo ->
                        val weatherInfoJson = Gson().toJson(weatherInfo)
                        navController.navigate(Screen.Details.createRoute(weatherInfoJson))
                    }
                )
            }
            composable(route = Screen.Alerts.rout) {
                ALertsScreen()
            }
            composable(route = Screen.Setting.rout) {
                SettingScreen()
            }
            composable(route = Screen.Map.rout) {
                PlacePicker(
                    viewModel(
                        factory = PlacesViewModelFactory(
                            WeatherDataRepository.getInstance(
                                WeatherRemoteDataSource(RetrofitHelper.weatherService),
                                WeatherLocalDatSource(
                                    WeatherDatabase.getInstance(LocalContext.current).getWeatherDao(),
                                    SharedPrefImp(LocalContext.current.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE))
                                )
                            ),
                            placesClient
                        )
                    ),
                    { latlon ->
                        action?.invoke(latlon)
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
                    WeatherDetailsScreen(it)
                }
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

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val selectedNavigationIndex = rememberSaveable {
        mutableIntStateOf(0)
    }
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect {
            val currentRoute = it.destination.route
            val index = navigationItems.indexOfFirst { it.route == currentRoute }
            if (index != -1) {
                selectedNavigationIndex.intValue = index
            }
        }

    }
    NavigationBar(
        containerColor = colorResource(R.color.Default_Colour)
    ) {
        navigationItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedNavigationIndex.intValue == index,
                onClick = {
                    selectedNavigationIndex.intValue = index
                    navController.navigate(item.route) {
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(imageVector = item.icon, contentDescription = item.title)
                },
                label = {
                    if (index == selectedNavigationIndex.intValue) {
                        Text(
                            item.title,
                            color = Color.White
                        )
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    unselectedIconColor = Color.LightGray,
                    selectedIconColor = Color.White,
                    indicatorColor = Color.DarkGray
                )

            )
        }
    }
}


@Composable
fun ALertsScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Cart Screen",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}

@Composable
fun SettingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Setting Screen",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}

