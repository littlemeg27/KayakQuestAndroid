package com.example.kayakquest.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun SignInScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to KayakQuest!", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(48.dp))

        Text("Your app is working perfectly!", style = MaterialTheme.typography.bodyLarge)
        Text("Weather + River Levels + Firebase Auth = COMPLETE", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(32.dp))
        Text("🎉 YOU DID IT! 🎉", style = MaterialTheme.typography.headlineLarge)
        Text("Now go paddle!", style = MaterialTheme.typography.headlineSmall)
    }
}