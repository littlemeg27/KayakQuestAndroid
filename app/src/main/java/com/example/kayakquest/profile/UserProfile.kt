package com.example.kayakquest.profile

data class UserProfile(
    val userId: String = "",              // Firebase Auth UID
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val bio: String = "",
    val favoriteRiver: String = "",
    val totalTrips: Int = 0,
    val totalDistanceKm: Double = 0.0,
    val preferences: Map<String, Boolean> = emptyMap() // e.g., "notifications", "darkMode"
)