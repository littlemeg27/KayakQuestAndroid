package com.example.kayakquest.auth

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.example.kayakquest.R  // ← THIS FIXES THE 'R' ERROR
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class GoogleSignInActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(this)

        launchCredentialManager()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun launchCredentialManager() {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(getString(R.string.default_web_client_id))
            .setFilterByAuthorizedAccounts(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    context = this@GoogleSignInActivity,
                    request = request
                )
                handleSignIn(result.credential)
            } catch (e: GetCredentialException) {
                Log.e(TAG, "Credential Manager failed", e)
            }
        }
    }

    private fun handleSignIn(credential: androidx.credentials.Credential) {
        when (credential) {
            is CustomCredential -> {
                if (credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
                }
            }
            else -> Log.w(TAG, "Unsupported credential type")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun signOut() {
        auth.signOut()
        lifecycleScope.launch {
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (e: ClearCredentialException) {
                Log.e(TAG, "Clear credential failed", e)
            }
        }
        updateUI(null)
    }

    private fun updateUI(user: com.google.firebase.auth.FirebaseUser?) {
        // Navigate or update UI based on user
        if (user != null) {
            // Signed in — go to main screen
            finish()
        }
    }

    companion object {
        private const val TAG = "GoogleSignInActivity"
    }
}