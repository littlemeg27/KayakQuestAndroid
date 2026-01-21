package com.example.paddlequest.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paddlequest.data.KayakerProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _profile = MutableStateFlow<KayakerProfile?>(null)
    val profile: StateFlow<KayakerProfile?> = _profile.asStateFlow()

    private val _profiles = MutableStateFlow<List<KayakerProfile>>(emptyList())
    val profiles: StateFlow<List<KayakerProfile>> = _profiles.asStateFlow()

    init {
        loadProfile()  // Load current user's profile
        loadAllProfiles()  // Load all profiles for sharing/float plans
    }

    private fun loadProfile() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val doc = firestore.collection("profiles").document(userId).get().await()
                _profile.value = doc.toObject(KayakerProfile::class.java)
            } catch (e: Exception) {
                Log.e("ProfileVM", "Error loading profile", e)
            }
        }
    }

    fun saveProfile(updated: KayakerProfile) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                firestore.collection("profiles").document(userId).set(updated).await()
                _profile.value = updated
            } catch (e: Exception) {
                Log.e("ProfileVM", "Error saving profile")
            }
        }
    }

    private fun loadAllProfiles() {
        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("profiles").get().await()
                _profiles.value = snapshot.toObjects(KayakerProfile::class.java)
            } catch (e: Exception) {
                Log.e("ProfileVM", "Error loading all profiles")
            }
        }
    }
}