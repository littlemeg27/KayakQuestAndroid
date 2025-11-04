package com.example.kayakquest.screens

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.kayakquest.R  // Ensure default_web_client_id is defined in res/values/strings.xml
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Composable
@Suppress("DEPRECATION")  // Suppress deprecation warnings for GoogleSignIn classes
fun SignInScreen() {
    val context = LocalContext.current
    val mAuth = FirebaseAuth.getInstance()
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))  // Resolved with R import; add to strings.xml if missing
        .requestEmail()
        .build()
    val mGoogleSignInClient = GoogleSignIn.getClient(context, gso)

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            task.addOnSuccessListener { account ->
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                mAuth.signInWithCredential(credential)
                    .addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            Log.d("SignIn", "Sign-in successful")
                            // TODO: Navigate to main screen or update UI
                        } else {
                            Log.e("SignIn", "Sign-in failed", authTask.exception)
                            // TODO: Show error message to user (e.g., Toast)
                        }
                    }
            }.addOnFailureListener { exception ->
                Log.e("SignIn", "Google account fetch failed", exception)
            }
        } else {
            Log.e("SignIn", "Sign-in canceled or failed with code: ${result.resultCode}")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            val signInIntent = mGoogleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }) {
            Text("Sign In with Google")
        }
    }
}