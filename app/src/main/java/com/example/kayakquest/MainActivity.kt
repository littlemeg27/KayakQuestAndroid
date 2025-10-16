package com.example.kayakquest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kayakquest.ui.theme.KayakQuestTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KayakQuestTheme {
                KayakQuestApp()
            }
        }
    }
}

@Composable
fun KayakQuestApp() {
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("KayakQuest") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                val currentRoute by navController.currentBackStackEntryAsState()
                val routes = listOf(
                    "signIn" to "Sign In" to Icons.Default.AccountCircle,
                    "map" to "Map" to Icons.Default.Map,
                    "floatPlan" to "Float Plan" to Icons.Default.Edit,
                    "weather" to "Weather" to Icons.Default.Cloud,
                    "settings" to "Settings" to Icons.Default.Settings
                )
                routes.forEach { (route, label, icon) ->
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) },
                        selected = currentRoute?.destination?.route == route,
                        onClick = {
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "signIn",
            modifier = androidx.compose.ui.Modifier.padding(padding)
        ) {
            composable("signIn") { SignInScreen() }
            composable("map") { MapScreen() }
            composable("floatPlan") { FloatPlanScreen() }
            composable("weather") { WeatherScreen() }
            composable("settings") { SettingsScreen() }
        }
    }
}