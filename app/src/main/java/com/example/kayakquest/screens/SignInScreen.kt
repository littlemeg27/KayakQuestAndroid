package com.example.kayakquest.screens

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.kayakquest.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.actionCodeSettings
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

@Composable
fun signInScreen() {  // ← lowercase name (Compose convention)
    val context = LocalContext.current
    val activity = context as ComponentActivity
    val scope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var verificationCode by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf("") }
    var isCodeSent by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Google Sign-In
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
            Log.e("SignIn", "Google failed", e)
            error = "Google sign-in failed"
        }
    }

    // Phone Auth Callbacks
    val phoneCallbacks = remember {
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                scope.launch {
                    auth.signInWithCredential(credential).await()
                    Log.d("SignIn", "Phone auto-verified!")
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.e("SignIn", "Phone verification failed", e)
                error = e.message ?: "Verification failed"
                isLoading = false
            }

            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                verificationId = id
                isCodeSent = true
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to KayakQuest", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(32.dp))

        // Google Sign-In
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

        // Email Link Sign-In
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = {
            if (email.isBlank()) {
                error = "Enter email"
                return@Button
            }
            isLoading = true
            scope.launch {
                try {
                    val settings = actionCodeSettings {
                        url = "https://kayakquest.page.link/signin"
                        handleCodeInApp = true
                        setAndroidPackageName("com.example.kayakquest", true, "12")
                    }
                    auth.sendSignInLinkToEmail(email, settings).await()
                    error = "Check your email for magic link!"
                } catch (e: Exception) {
                    error = "Email failed: ${e.message}"
                } finally {
                    isLoading = false
                }
            }
        }, enabled = !isLoading) {
            Text(if (isLoading) "Sending..." else "Sign In with Email")
        }

        Spacer(Modifier.height(16.dp))

        // Phone Auth
        if (!isCodeSent) {
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number (+1...)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = {
                if (phone.isBlank()) {
                    error = "Enter phone number"
                    return@Button
                }
                isLoading = true
                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(phone)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(activity)
                    .setCallbacks(phoneCallbacks)
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }, enabled = !isLoading) {
                Text("Send Code")
            }
        } else {
            OutlinedTextField(
                value = verificationCode,
                onValueChange = { verificationCode = it },
                label = { Text("Verification Code") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = {
                if (verificationCode.isBlank()) {
                    error = "Enter code"
                    return@Button
                }
                isLoading = true
                val credential = PhoneAuthProvider.getCredential(verificationId, verificationCode)
                scope.launch {
                    try {
                        auth.signInWithCredential(credential).await()
                        Log.d("SignIn", "Phone success!")
                    } catch (e: Exception) {
                        error = "Phone sign-in failed"
                    } finally {
                        isLoading = false
                    }
                }
            }) {
                Text("Verify")
            }
        }

        error?.let {
            Spacer(Modifier.height(16.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}