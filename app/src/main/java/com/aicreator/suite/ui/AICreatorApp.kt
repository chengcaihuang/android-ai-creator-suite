package com.aicreator.suite.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

/**
 * 主应用导航
 *
 * 底部导航 + 页面切换
 */
@Composable
fun AICreatorApp(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen()
            }
            composable(Screen.Create.route) {
                CreateScreen()
            }
            composable(Screen.Publish.route) {
                PublishScreen()
            }
            composable(Screen.Monetize.route) {
                MonetizeScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen()
            }
        }
    }
}

/**
 * 底部导航项
 */
sealed class Screen(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    object Home : Screen("home", "首页", Icons.Default.Home)
    object Create : Screen("create", "创作", Icons.Default.Create)
    object Publish : Screen("publish", "分发", Icons.Default.Share)
    object Monetize : Screen("monetize", "变现", Icons.Default.AttachMoney)
    object Profile : Screen("profile", "我的", Icons.Default.Person)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Create,
    Screen.Publish,
    Screen.Monetize,
    Screen.Profile
)
