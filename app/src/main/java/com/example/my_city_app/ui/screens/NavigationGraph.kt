package com.example.my_city_app.ui.screens

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.my_city_app.data.utils.Category

const val ROUTE_HOME = "Home"
const val CITY = "City"
const val ROUTE_CATEGORY = "Category"
const val CATEGORY = "CatArg"
const val ROUTE_CITY_CATEGORIES = "$ROUTE_CATEGORY/{$CITY}"
const val ROUTE_CITY_CATEGORY = "$ROUTE_CITY_CATEGORIES/{$CATEGORY}"
const val ROUTE_RECOMM_CREATE = "$ROUTE_CITY_CATEGORY/Create"

@Composable
fun MyCityNavHost(
    windowSize: WindowWidthSizeClass,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = ROUTE_HOME, modifier = modifier) {
        composable(route = ROUTE_HOME) {
            HomeScreen(navigateToCategories = navController::navigate)
        }
        composable(route = ROUTE_CITY_CATEGORIES, arguments = listOf(navArgument(CITY){
            type = NavType.IntType
        })
        ) {
            RecommendationScreen(
                onBackClicked = {
                    if(navController.previousBackStackEntry != null)
                        navController.popBackStack()
                },
                onCreateClick = navController::navigate,
                windowMode = windowSize,
            )
        }
        composable(
            route = ROUTE_RECOMM_CREATE,
            arguments = listOf(
                navArgument(CITY) {
                    type = NavType.IntType
                },
                navArgument(CATEGORY) {
                    type = NavType.EnumType(type = Category::class.java)
                }
            )
        ) {
            RecommCreateView(onBack = navController::popBackStack)
        }
    }
}