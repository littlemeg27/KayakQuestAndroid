package com.example.paddlequest.screens

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.paddlequest.R
import com.example.paddlequest.navigation.Screen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.navigation.NavController
import androidx.compose.ui.res.stringResource

@Composable
fun SignInScreen(navController: NavController)
{
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val coroutineScope = rememberCoroutineScope()
    val clientId = stringResource(R.string.default_web_client_id)

    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Google Sign-In launcher
    val googleSignInLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            isLoading = true
            coroutineScope.launch {
                try {
                    auth.signInWithCredential(credential).await()
                    Log.d("SignIn", "Google sign-in successful!")

                    // Navigate to Map screen and remove SignIn from back stack
                    navController.navigate(Screen.Map.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                } catch (e: Exception) {
                    Log.e("SignIn", "Google sign-in failed", e)
                    error = e.localizedMessage ?: "Sign-in failed"
                } finally {
                    isLoading = false
                }
            }
        } catch (e: ApiException) {
            Log.e("SignIn", "Google sign-in failed", e)
            error = "Google sign-in failed: ${e.statusCode}"
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Welcome to PaddleQuest",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(Modifier.height(48.dp))

            if (isLoading) {
                CircularProgressIndicator()
                Spacer(Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    error = null
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(clientId)
                        .requestEmail()
                        .build()
                    val googleSignInClient = GoogleSignIn.getClient(context, gso)
                    googleSignInLauncher.launch(googleSignInClient.signInIntent)
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign in with Google")
            }

            error?.let {
                Spacer(Modifier.height(16.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(32.dp))
            Text("Or sign in with email/phone later...", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
