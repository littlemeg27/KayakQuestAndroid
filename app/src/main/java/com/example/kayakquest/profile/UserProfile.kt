package com.example.kayakquest.profile

data class UserProfile(
    val userId: String = "",              // Firebase Auth UID
    val photoUrl: String? = null,
    val name: String = "",
    val gender: String = "",
    val age: String = "",
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val email: String = "",
    val phone: String = "",
    val emergencyContactName: String = "",
    val emergencyPhone: String = "",
    val kayakMake: String = "",
    val kayakModel: String = "",
    val kayakLength: Double = 0.0,
    val kayakColor: String = "",
    val safetyEquipmentNotes: String = "",
    val vehicleMake: String = "",
    val vehicleModel: String = "",
    val vehicleColor: String = "",
    val plateNumber: String = "",
    val favoriteRiver: String = "",
    val totalTrips: Int = 0,
    val totalDistanceKm: Double = 0.0,
    val preferences: Map<String, Boolean> = emptyMap() // e.g., "notifications", "darkMode"
)
