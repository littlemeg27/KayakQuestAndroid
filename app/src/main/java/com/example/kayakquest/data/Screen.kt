package com.example.kayakquest.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning

sealed class Screen(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
{
    data object SignIn : Screen("signIn", "Sign In", Icons.Default.AccountCircle)
    data object Map : Screen("map", "Map", Icons.Default.Place)
    data object FloatPlan : Screen("floatPlan", "Float Plan", Icons.Default.Create)
    data object Weather : Screen("weather", "Weather", Icons.Default.Warning)
    data object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}