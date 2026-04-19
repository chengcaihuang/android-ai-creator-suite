package com.aicreator.suite.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aicreator.suite.ui.screens.home.HomeScreen
import com.aicreator.suite.ui.screens.create.CreateScreen
import com.aicreator.suite.ui.screens.publish.PublishScreen
import com.aicreator.suite.ui.screens.monetize.MonetizeScreen
import com.aicreator.suite.ui.screens.profile.ProfileScreen

/**
 * 应用导航路由
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Create : Screen("create")
    object Publish : Screen("publish")
    object Monetize : Screen("monetize")
    object Profile : Screen("profile")
}

/**
 * 应用导航控制器
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToCreate = { navController.navigate(Screen.Create.route) },
                onNavigateToPublish = { navController.navigate(Screen.Publish.route) },
                onNavigateToMonetize = { navController.navigate(Screen.Monetize.route) }
            )
        }

        composable(Screen.Create.route) {
            CreateScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Publish.route) {
            PublishScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Monetize.route) {
            MonetizeScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
