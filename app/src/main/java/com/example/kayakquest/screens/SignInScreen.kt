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
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.example.kayakquest.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
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
    val scope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()
    val credentialManager = CredentialManager.create(context)

    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fallback launcher for classic Google Sign-In
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try
        {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            scope.launch {
                auth.signInWithCredential(credential).await()
                Log.d("SignIn", "Classic sign-in successful")
            }
        }
        catch (e: Exception)
        {
            Log.e("SignIn", "Classic sign-in failed", e)
            errorMessage = "Sign-in failed"
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick =
            {
            errorMessage = null
            scope.launch {
                try
                {
                    // Try One Tap first
                    val result = credentialManager.getCredential(
                        request = GetCredentialRequest.Builder()
                            .addCredentialOption(
                                GetGoogleIdOption.Builder()
                                    .setFilterByAuthorizedAccounts(false)
                                    .setServerClientId(context.getString(R.string.default_web_client_id))
                                    .setNonce(generateNonce())
                                    .setAutoSelectEnabled(true)
                                    .build()
                            )
                            .build(),
                        context = context
                    )
                    handleOneTapSignIn(result, auth)
                } catch (e: GetCredentialException)
                {
                    // One Tap failed or canceled — fall back to classic flow
                    Log.d("SignIn", "One Tap not available, using classic flow")
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(context.getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()
                    val client = GoogleSignIn.getClient(context, gso)
                    launcher.launch(client.signInIntent)
                }
                catch (e: Exception)
                {
                    Log.e("SignIn", "Sign-in failed", e)
                    errorMessage = "Sign-in failed"
                }
            }
        })
        {
            Text("Sign In with Google")
        }

        if (errorMessage != null)
        {
            Spacer(modifier = Modifier.height(16.dp))
            Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
        }
    }
}

private suspend fun handleOneTapSignIn(result: GetCredentialResponse, auth: FirebaseAuth)
{
    val credential = result.credential
    if (credential is GoogleIdTokenCredential)
    {
        val idToken = credential.idToken
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(firebaseCredential).await()
        Log.d("SignIn", "One Tap sign-in successful!")
    }
}

private fun generateNonce(): String
{
    val rawNonce = UUID.randomUUID().toString()
    val bytes = rawNonce.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    return digest.fold("") { str, it -> str + "%02x".format(it) }
}