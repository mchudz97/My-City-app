package com.example.my_city_app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.my_city_app.ui.screens.MyCityNavHost
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

@Composable
fun MyCityApp(
    windowSize: WindowWidthSizeClass,
    navController: NavHostController = rememberNavController()
) {
    MyCityNavHost(windowSize = windowSize, navController = navController)
}