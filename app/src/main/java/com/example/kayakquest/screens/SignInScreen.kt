package com.example.kayakquest.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.NoCredentialException
import com.example.kayakquest.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID

@Composable
fun SignInScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = CredentialManager.create(context)
    val auth = FirebaseAuth.getInstance()

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            errorMessage = null
            scope.launch {
                try {
                    val result = getGoogleIdTokenCredential(credentialManager, context)
                    handleSignIn(result, auth)
                } catch (e: NoCredentialException) {
                    // This is NOT an error – user just canceled
                    Log.d("SignIn", "User canceled One Tap sign-in")
                    errorMessage = "Sign-in canceled. Tap again to try."
                } catch (e: Exception) {
                    Log.e("SignIn", "Sign-in failed", e)
                    errorMessage = "Sign-in failed. Check internet or try again."
                }
            }
        }) {
            Text("Sign In with Google")
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
        }
    }
}

private suspend fun getGoogleIdTokenCredential(
    credentialManager: CredentialManager,
    context: android.content.Context
): GetCredentialResponse {
    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(context.getString(R.string.default_web_client_id))
        .setNonce(generateNonce())
        .setAutoSelectEnabled(true)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    return credentialManager.getCredential(
        request = request,
        context = context
    )
}

private fun generateNonce(): String {
    val rawNonce = UUID.randomUUID().toString()
    val bytes = rawNonce.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    return digest.fold("") { str, it -> str + "%02x".format(it) }
}

private suspend fun handleSignIn(result: GetCredentialResponse, auth: FirebaseAuth) {
    val credential = result.credential

    if (credential is com.google.android.libraries.identity.googleid.GoogleIdTokenCredential) {
        val idToken = credential.idToken
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(firebaseCredential).await()
        Log.d("SignIn", "Sign-in successful!")
        // TODO: Navigate to main screen
    }
}