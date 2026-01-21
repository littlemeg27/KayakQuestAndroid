package com.example.paddlequest.operations

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.paddlequest.data.Screen
import com.example.paddlequest.screens.*
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

        setContent {
            MaterialTheme {
                PaddleQuestApp()
            }
        }
    }
}

@Composable
fun PaddleQuestApp()
{
    val navController = rememberNavController()

    val items = listOf(
        Screen.Map,
        Screen.FloatPlan,
        Screen.Weather,
        Screen.Settings,
        Screen.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
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
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.SignIn.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.SignIn.route) { SignInScreen(navController) }
            composable(Screen.Map.route) { MapScreen() }
            composable(Screen.FloatPlan.route) { FloatPlanScreen() }
            composable(Screen.Weather.route) { WeatherScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
            composable(Screen.Profile.route) { ProfileScreen() }
        }
    }
}
