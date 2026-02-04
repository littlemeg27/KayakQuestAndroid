package com.example.paddlequest.operations

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try
        {
            Log.d("MainActivity", "FirebaseApp initialized: ${FirebaseApp.getInstance().name}")
        }
        catch (e: Exception)
        {
            Log.e("MainActivity", "Firebase not initialized", e)
        }

        setContent {
            MaterialTheme {
                PaddleQuestApp()
            }
        }
    }
}
