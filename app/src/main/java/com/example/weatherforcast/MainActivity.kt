package com.example.weatherforcast

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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.example.weatherforcast.homeScreen.HomeScreen
import com.example.weatherforcast.homeScreen.HomeViewModelFactory
import com.example.weatherforcast.model.remote.RetrofitHelper
import com.example.weatherforcast.model.remote.WeatherRemoteDataSource
import com.example.weatherforcast.model.repositories.WeatherDataRepository
import com.example.weatherforcast.ui.theme.WeatherForcastTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherForcastTheme {
                WeatherApp()
            }
        }
    }

}

@Composable
fun MySplashScreen(action:  () -> Unit) {
    val alpha= remember {
        Animatable(0f)
    }
    LaunchedEffect(key1 =true ) {
        alpha.animateTo(1f,
            animationSpec = tween(1500,)
        )
        delay(2000)
        action()
    }
    Box (modifier = Modifier.fillMaxSize()
        .background(colorResource(R.color.Default_Colour)),
        contentAlignment = Alignment.Center){
        Image(painter = painterResource(R.drawable.splash), contentDescription = "Splash Screen")
    }
}

@Composable
private fun WeatherApp() {
    val navController= rememberNavController()
    val currentRoute= remember { mutableStateOf(Screen.Splash.rout) }
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect{
            currentRoute.value=it.destination.route ?:Screen.Home.rout
        }
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = {
            //val currentRout=navController.currentBackStackEntry?.destination?.route
            if(currentRoute.value!=Screen.Splash.rout)
            {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.rout,
            modifier = Modifier.padding(innerPadding)
        ){
            composable(route = Screen.Splash.rout) {
                MySplashScreen {
                    navController.navigate(Screen.Home.rout){
                        popUpTo(Screen.Splash.rout) {inclusive=true  }
                    }
                }
            }
            composable(route = Screen.Home.rout) {
                HomeScreen(
                    viewModel(
                        factory = HomeViewModelFactory(
                        WeatherDataRepository.getInstance(
                            WeatherRemoteDataSource(RetrofitHelper.weatherService)
                        )))

                )
            }
            composable(route = Screen.Favourites.rout) {
                FavouritesScreen()
            }
            composable(route = Screen.Alerts.rout) {
                ALertsScreen()
            }
            composable(route = Screen.Setting.rout) {
                SettingScreen()
            }
        }
        BackHandler (enabled = currentRoute.value!=Screen.Home.rout){
            navController.navigate(Screen.Home.rout){
                popUpTo(Screen.Home.rout){
                    inclusive=false
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
        navController.currentBackStackEntryFlow.collect{
            val currentRoute=it.destination.route
            val index= navigationItems.indexOfFirst { it.route==currentRoute }
            if(index!=-1){
                selectedNavigationIndex.intValue=index
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
                    navController.navigate(item.route){
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(imageVector = item.icon, contentDescription = item.title)
                },
                label = {if (index == selectedNavigationIndex.intValue)
                {
                    Text(
                        item.title,
                        color =Color.White
                    )
                }},
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
fun FavouritesScreen(){
    Box (modifier = Modifier
        .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = "Profile Screen",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}


@Composable
fun ALertsScreen(){
    Box (modifier = Modifier
        .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = "Cart Screen",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}

@Composable
fun SettingScreen(){
    Box (modifier = Modifier
        .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = "Setting Screen",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}

