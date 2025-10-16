package com.example.kayakquest.data

data class FloatPlan(
    var kayakerName: String = "",
    var gender: String = "",
    var phoneNumber: String = "",
    var age: Int = 0,
    var address: String = "",
    var city: String = "",
    var state: String = "",
    var emergencyContact: String = "",
    var emergencyPhone: String = "",
    var kayakMake: String = "",
    var kayakModel: String = "",
    var kayakLength: String = "",
    var kayakColor: String = "",
    var safetyEquipmentNotes: String = "",
    var vehicleMake: String = "",
    var vehicleColor: String = "",
    var vehicleModel: String = "",
    var plateNumber: String = "",
    var departureDate: String = "",
    var departureTime: String = "",
    var putInLocation: String = "",
    var takeOutLocation: String = "",
    var returnTime: String = "",
    var tripNotes: String = "",
    var userId: String = "",
    var pdfUrl: String? = null
)
{
    fun toMap(): Map<String, Any?> = mapOf(
        "kayakerName" to kayakerName,
        "gender" to gender,
        // ... all fields
        "pdfUrl" to pdfUrl
    )
}