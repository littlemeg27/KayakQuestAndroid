package com.example.kayakquest.data

import com.google.firebase.firestore.PropertyName

data class KayakerProfile(
    @PropertyName("userId") val userId: String = "",
    @PropertyName("name") val name: String = "",
    @PropertyName("gender") val gender: String = "",
    @PropertyName("age") val age: Int = 0,
    @PropertyName("address") val address: String = "",
    @PropertyName("city") val city: String = "",
    @PropertyName("state") val state: String = "",
    @PropertyName("email") val email: String = "",
    @PropertyName("phone") val phone: String = "",
    @PropertyName("safetyEquipmentNotes") val safetyEquipmentNotes: String = "",
    @PropertyName("vehicleMake") val vehicleMake: String = "",
    @PropertyName("vehicleModel") val vehicleModel: String = "",
    @PropertyName("vehicleColor") val vehicleColor: String = "",
    @PropertyName("plateNumber") val plateNumber: String = "",

    @PropertyName("craftEntries")
    val craftEntries: List<CraftEntry> = emptyList()
) {
    val kayaks get() = craftEntries.filter { it.type == "Kayak" }
    val canoes get() = craftEntries.filter { it.type == "Canoe" }
    val paddleBoards get() = craftEntries.filter { it.type == "PaddleBoard" }
}