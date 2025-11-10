package com.example.kayakquest.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean
)

val items = listOf(
    BottomNavigationItem("Home", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle, false),
    BottomNavigationItem("Map", Icons.Filled.Place, Icons.Outlined.Place, false),
    BottomNavigationItem("Float Plan", Icons.Filled.Create, Icons.Outlined.Create, false),
    BottomNavigationItem("Weather", Icons.Filled.Warning, Icons.Outlined.Warning, true),
    BottomNavigationItem("Settings", Icons.Filled.Settings, Icons.Outlined.Settings, false),

)
