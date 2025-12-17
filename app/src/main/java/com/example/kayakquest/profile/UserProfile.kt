package com.example.kayakquest.profile

import com.google.firebase.firestore.PropertyName

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


    val vehicleModel: String = "",

    val favoriteRiver: String = "",
    val totalTrips: Int = 0,
    val totalDistanceKm: Double = 0.0,
    val preferences: Map<String, Boolean> = emptyMap() // e.g., "notifications", "darkMode"
)


@PropertyName("userId") val userId: String = "",  // Firebase Auth UID
@PropertyName("name") val name: String = "",
@PropertyName("gender") val gender: String = "",
@PropertyName("age") val age: Int = 0,
@PropertyName("address") val address: String = "",
@PropertyName("city") val city: String = "",
@PropertyName("state") val state: String = "",
@PropertyName("email") val email: String = "",
@PropertyName("phone") val phone: String = "",
@PropertyName("kayakMake") val kayakMake: String = "",
@PropertyName("kayakModel") val kayakModel: String = "",
@PropertyName("kayakLength") val kayakLength: String = "",
@PropertyName("kayakColor") val kayakColor: String = "",
@PropertyName("safetyEquipmentNotes") val safetyEquipmentNotes: String = "",
@PropertyName("vehicleMake") val vehicleMake: String = "",
@PropertyName("vehicleModel") val vehicleModel: String = "",
@PropertyName("vehicleColor") val vehicleColor: String = "",
@PropertyName("plateNumber") val plateNumber: String = ""