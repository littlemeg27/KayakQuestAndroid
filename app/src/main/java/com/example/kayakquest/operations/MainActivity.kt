package com.example.kayakquest.operations

import android.os.Bundle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.filled.*
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.kayakquest.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        try
        {
            Log.d("MainActivity", "FirebaseApp initialized: ${FirebaseApp.getInstance().name}")
        } catch (e: Exception)
        {
            Log.e("MainActivity", "Firebase not initialized", e)
        }
        setContent
        {
            KayakQuestApp()
        }
    }
}

@Composable
fun KayakQuestApp()
{
    val navController = rememberNavController()
    val items = listOf(
        Screen.SignIn,
        Screen.Map,
        Screen.FloatPlan,
        Screen.Weather,
        Screen.Settings
    )

    Scaffold(
        bottomBar =
            {
            NavigationBar
                {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach {
                    screen ->
                    NavigationBarItem(
                        icon = { Icon(painterResource(id = screen.icon), contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick =
                            {
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
    )
    {
        innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.SignIn.route,
            modifier = Modifier.padding(innerPadding)
        )
        {
            composable(Screen.SignIn.route) { SignInScreen(navController) }
            composable(Screen.Map.route) { MapScreen(navController) }
            composable(Screen.FloatPlan.route) { FloatPlanScreen(navController) }
            composable(Screen.Weather.route) { WeatherScreen(navController) }
            composable(Screen.Settings.route) { SettingsScreen(navController) }
        }
    }
}

sealed class Screen(val route: String, val label: String, val icon: Int) {
    data object SignIn : Screen("signIn", "Sign In", R.drawable.ic_sign_in)  // Replace with your actual drawable resources
    data object Map : Screen("map", "Map", R.drawable.ic_map)
    data object FloatPlan : Screen("floatPlan", "Float Plan", R.drawable.ic_float_plan)
    data object Weather : Screen("weather", "Weather", R.drawable.ic_weather)
    data object Settings : Screen("settings", "Settings", R.drawable.ic_settings)
}