package com.example.paddlequest.operations

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.paddlequest.screens.FloatPlanScreen
import com.example.paddlequest.screens.MapScreen
import com.example.paddlequest.screens.ProfileScreen
import com.example.paddlequest.screens.SettingsScreen
import com.example.paddlequest.screens.SignInScreen
import com.example.paddlequest.screens.WeatherScreen

@Composable
fun PaddleQuestApp()
{
    val navController = rememberNavController()

    val bottomNavItems = listOf(
        Screen.Map,
        Screen.FloatPlan,
        Screen.Weather,
        Screen.Settings,
        Screen.Profile
    )

    Scaffold(
        bottomBar =
            {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (selected) screen.icon else when (screen)
                                {
                                    is Screen.Map -> Icons.Outlined.Place
                                    is Screen.FloatPlan -> Icons.Outlined.Create
                                    is Screen.Weather -> Icons.Outlined.Warning
                                    is Screen.Settings -> Icons.Outlined.Settings
                                    is Screen.Profile -> Icons.Outlined.Person
                                    else -> Icons.Outlined.AccountCircle
                                },
                                contentDescription = screen.label
                            )
                        },
                        label = { Text(screen.label) },
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route)
                            {
                                popUpTo(navController.graph.findStartDestination().id)
                                {
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
    ) {
        innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.SignIn.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.SignIn.route) { SignInScreen(navController) }
            composable(Screen.Map.route) { MapScreen() }
            composable(Screen.FloatPlan.route) { FloatPlanScreen() }
            composable(Screen.Weather.route) { WeatherScreen() }
            composable(Screen.Profile.route) { ProfileScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }
}