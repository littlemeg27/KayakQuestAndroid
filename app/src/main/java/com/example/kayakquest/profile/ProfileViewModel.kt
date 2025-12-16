package com.example.kayakquest.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kayakquest.data.KayakerProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _profile = MutableLiveData<KayakerProfile?>()
    val profile: LiveData<KayakerProfile?> = _profile

    private val _profiles = MutableLiveData<List<KayakerProfile>>()
    val profiles: LiveData<List<KayakerProfile>> = _profiles

    init {
        loadProfile()  // Load current user's profile on init
        loadAllProfiles()  // Load all for float plan sharing
    }

    fun loadProfile() {
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

    fun saveProfile(profile: KayakerProfile) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                firestore.collection("profiles").document(userId).set(profile).await()
                _profile.value = profile
            } catch (e: Exception) {
                Log.e("ProfileVM", "Error saving profile", e)
            }
        }
    }

    fun loadAllProfiles() {
        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("profiles").get().await()
                _profiles.value = snapshot.toObjects(KayakerProfile::class.java)
            } catch (e: Exception) {
                Log.e("ProfileVM", "Error loading profiles", e)
            }
        }
    }
}