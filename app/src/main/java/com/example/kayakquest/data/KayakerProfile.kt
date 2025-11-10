package com.example.kayakquest.data

import com.google.firebase.firestore.PropertyName

data class KayakerProfile(
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
) {
    // Empty constructor for Firestore
    constructor() :
            this(
                "",
                "",
                "",
                0,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "")
}