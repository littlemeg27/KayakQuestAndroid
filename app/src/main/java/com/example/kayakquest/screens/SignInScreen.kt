package com.example.kayakquest.screens

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
import com.example.kayakquest.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.actionCodeSettings
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun SignInScreen()
{
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isSendingLink by remember { mutableStateOf(false) }

    val googleLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            scope.launch {
                auth.signInWithCredential(credential).await()
                Log.d("SignIn", "Google success!")
            }
        } catch (e: ApiException) {
            Log.e("SignIn", "Google failed: ${e.statusCode}", e)
            error = "Google sign-in failed"
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Google Sign-In Button
        Button(onClick = {
            error = null
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val client = GoogleSignIn.getClient(context, gso)
            googleLauncher.launch(client.signInIntent)
        }) {
            Text("Sign In with Google")
        }

        Spacer(Modifier.height(16.dp))

        // Email Input
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // Email Link Button
        Button(
            onClick = {
                error = null
                if (email.isBlank()) {
                    error = "Enter your email"
                    return@Button
                }
                isSendingLink = true
                scope.launch {
                    try {
                        val actionCodeSettings = actionCodeSettings {
                            url = "https://kayakquest.page.link/signin"
                            handleCodeInApp = true
                            setAndroidPackageName("com.example.kayakquest", true, "12")
                        }

                        auth.sendSignInLinkToEmail(email, actionCodeSettings).await()
                        Log.d("SignIn", "Email link sent to $email")
                        error = "Check your email for magic link!"
                    } catch (e: Exception) {
                        Log.e("SignIn", "Email link failed", e)
                        error = "Failed to send email: ${e.message}"
                    } finally {
                        isSendingLink = false
                    }
                }
            },
            enabled = !isSendingLink
        ) {
            if (isSendingLink) {
                Text("Sending...")
            } else {
                Text("Sign In with Email Link")
            }
        }

        error?.let {
            Spacer(Modifier.height(16.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}