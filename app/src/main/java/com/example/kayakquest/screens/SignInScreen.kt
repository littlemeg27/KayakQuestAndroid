package com.example.kayakquest.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.example.kayakquest.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID

@Composable
fun SignInScreen()
{
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = CredentialManager.create(context)
    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Button(onClick =
            {
            coroutineScope.launch {
                try
                {
                    val result = getGoogleIdTokenCredential(credentialManager, context)
                    handleSignIn(result, auth)
                } catch (e: Exception) {
                    Log.e("SignIn", "Sign-in failed", e)
                }
            }
        }) {
            Text("Sign In with Google")
        }
    }
}

private suspend fun getGoogleIdTokenCredential(
    credentialManager: CredentialManager,
    context: android.content.Context
): GetCredentialResponse {
    val request = GetCredentialRequest.Builder()
        .addCredentialOption(
            GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .setNonce(generateNonce())
                .setAutoSelectEnabled(true)
                .build()
        )
        .build()

    return credentialManager.getCredential(request = request, context = context)
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
    if (credential is CustomCredential) {
        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val authCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                auth.signInWithCredential(authCredential).await()
                Log.d("SignIn", "Sign-in successful")
                // TODO: Navigate to home screen
            } catch (e: GoogleIdTokenParsingException) {
                Log.e("SignIn", "Invalid Google ID token", e)
            }
        } else {
            Log.e("SignIn", "Credential is not Google ID token")
        }
    } else {
        Log.e("SignIn", "Credential is not CustomCredential")
    }
}

// Optional: Sign Out Function
private suspend fun signOut(credentialManager: CredentialManager) {
    credentialManager.clearCredentialState(ClearCredentialStateRequest())
    FirebaseAuth.getInstance().signOut()
    Log.d("SignIn", "Signed out")
}