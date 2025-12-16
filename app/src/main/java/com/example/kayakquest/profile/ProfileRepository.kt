package com.example.kayakquest.profile

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProfileRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun loadProfile(): UserProfile? {
        val userId = auth.currentUser?.uid ?: return null
        return db.collection("users").document(userId)
            .get().await()
            .toObject(UserProfile::class.java)
    }

    suspend fun saveSection(userId: String, updates: Map<String, Any>) {
        db.collection("users").document(userId)
            .update(updates).await()
    }
}