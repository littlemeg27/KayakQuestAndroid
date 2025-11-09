package com.example.kayakquest.screens

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.kayakquest.operations.FloatPlan
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun FloatPlanScreen() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val floatPlan = remember { mutableStateOf(FloatPlan()) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Example TextFields for fields; add more as needed
        TextField(
            value = floatPlan.value.kayakerName,
            onValueChange = { newValue -> floatPlan.value = floatPlan.value.copy(kayakerName = newValue) },
            label = { Text("Kayaker Name") }
        )
        TextField(
            value = floatPlan.value.gender,
            onValueChange = { newValue -> floatPlan.value = floatPlan.value.copy(gender = newValue) },
            label = { Text("Gender") }
        )
        // Add similar TextFields for other FloatPlan fields, e.g.:
        // TextField(value = floatPlan.value.phoneNumber, onValueChange = { floatPlan.value = floatPlan.value.copy(phoneNumber = it) }, label = { Text("Phone Number") })
        // For dropdowns (gender, state): Use ExposedDropdownMenuBox
        // For dates/times: Use DatePicker/TimePicker (import androidx.compose.material3.DatePicker, etc.; may need @ExperimentalMaterial3Api)

        Button(onClick = {
            coroutineScope.launch {
                createAndUploadPdf(context, floatPlan.value)
            }
        }) {
            Text("Submit Float Plan")
        }
    }
}

private suspend fun createAndUploadPdf(context: Context, floatPlan: FloatPlan) {
    withContext(Dispatchers.IO) {
        try {
            val pdfFile = File(context.filesDir, "float_plan.pdf")
            val pdfWriter = PdfWriter(pdfFile.absolutePath)
            val pdfDoc = PdfDocument(pdfWriter)
            val document = Document(pdfDoc)

            // Add content as paragraphs (add all fields)
            document.add(Paragraph("Kayaker Name: ${floatPlan.kayakerName}"))
            document.add(Paragraph("Gender: ${floatPlan.gender}"))
            document.add(Paragraph("Phone Number: ${floatPlan.phoneNumber}"))
            document.add(Paragraph("Age: ${floatPlan.age}"))
            document.add(Paragraph("Address: ${floatPlan.address}, ${floatPlan.city}, ${floatPlan.state}"))
            document.add(Paragraph("Emergency Contact: ${floatPlan.emergencyContact} - ${floatPlan.emergencyPhone}"))
            document.add(Paragraph("Kayak Make: ${floatPlan.kayakMake}, Model: ${floatPlan.kayakModel}, Length: ${floatPlan.kayakLength}, Color: ${floatPlan.kayakColor}"))
            document.add(Paragraph("Safety Equipment Notes: ${floatPlan.safetyEquipmentNotes}"))
            document.add(Paragraph("Vehicle Make: ${floatPlan.vehicleMake}, Model: ${floatPlan.vehicleModel}, Color: ${floatPlan.vehicleColor}, Plate: ${floatPlan.plateNumber}"))
            document.add(Paragraph("Departure Date: ${floatPlan.departureDate}, Time: ${floatPlan.departureTime}"))
            document.add(Paragraph("Put In Location: ${floatPlan.putInLocation}"))
            document.add(Paragraph("Take Out Location: ${floatPlan.takeOutLocation}, Return Time: ${floatPlan.returnTime}"))
            document.add(Paragraph("Trip Notes: ${floatPlan.tripNotes}"))

            document.close()

            val auth = FirebaseAuth.getInstance()
            val userId = auth.currentUser?.uid ?: return@withContext
            val storageRef = FirebaseStorage.getInstance().reference
            val pdfRef = storageRef.child("float_plans/$userId/${pdfFile.name}")
            pdfRef.putFile(Uri.fromFile(pdfFile)).await()

            // Get download URL
            val uri = pdfRef.downloadUrl.await()
            floatPlan.pdfUrl = uri.toString()

            // Save to Firestore
            FirebaseFirestore.getInstance().collection("float_plans").add(floatPlan.toMap()).await()
        } catch (e: Exception) {
            // Handle error, e.g., log or show toast
            e.printStackTrace()
        }
    }
}