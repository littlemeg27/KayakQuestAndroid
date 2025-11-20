package com.example.kayakquest.operations

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kayakquest.data.Screen
import com.example.kayakquest.screens.FloatPlanScreen
import com.example.kayakquest.screens.MapScreen
import com.example.kayakquest.screens.ProfileScreen
import com.example.kayakquest.screens.SettingsScreen
import com.example.kayakquest.screens.SignInScreen
import com.example.kayakquest.screens.WeatherScreen
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        try
        {
            Log.d("MainActivity", "FirebaseApp initialized: ${FirebaseApp.getInstance().name}")
        }
        catch (e: Exception)
        {
            Log.e("MainActivity", "Firebase not initialized", e)
        }
        setContent{
            KayakQuestApp()
        }
    }
}

@Composable
fun KayakQuestApp()
{
    MaterialTheme{


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
            composable(Screen.SignIn.route) { SignInScreen() }
            composable(Screen.Map.route) { MapScreen() }
            composable(Screen.FloatPlan.route) { FloatPlanScreen() }
            composable(Screen.Weather.route) { WeatherScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
            composable("profile") { ProfileScreen() }
        }
    }

    }

}
