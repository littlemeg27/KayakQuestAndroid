package com.example.kayakquest.screens

import android.graphics.pdf.PdfDocument
import android.provider.DocumentsContract
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kayakquest.operations.FloatPlan
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun FloatPlanScreen() {
    val scrollState = rememberScrollState()
    val floatPlan = remember { mutableStateOf(FloatPlan()) }
    // Add states for each field, e.g., val kayakerName = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // TextFields for all fields, e.g.:
        TextField(value = floatPlan.value.kayakerName, onValueChange = { floatPlan.value = floatPlan.value.copy(kayakerName = it) }, label = { Text("Kayaker Name") })
        // DropdownMenu for spinners (gender, state, kayak color, etc.)
        // DatePickerDialog and TimePickerDialog for dates/times (use androidx.compose.material3.DatePicker and TimePicker)

        Button(onClick = {
            // Validate fields
            createAndUploadPdf(floatPlan.value)
        }) {
            Text("Submit Float Plan")
        }
    }
}

private fun createAndUploadPdf(floatPlan: FloatPlan) {
    // Similar to Java, use coroutines
    kotlinx.coroutines.GlobalScope.launch(Dispatchers.IO) {
        try {
            val pdfFile = File("float_plan.pdf")
            val pdfWriter = PdfWriter(pdfFile.path)
            val pdfDoc = PdfDocument(pdfWriter)
            val document = DocumentsContract.Document(pdfDoc)
            // Add paragraphs as in Java
            document.close()

            // Upload to Firebase
            val auth = FirebaseAuth.getInstance()
            val storageRef = FirebaseStorage.getInstance().reference
            val pdfRef = storageRef.child("float_plans/${auth.currentUser?.uid}/${pdfFile.name}")
            pdfRef.putFile(android.net.Uri.fromFile(pdfFile))
                .addOnSuccessListener {
                    // Get URL and save metadata to Firestore
                    pdfRef.downloadUrl.addOnSuccessListener { uri ->
                        floatPlan.pdfUrl = uri.toString()
                        FirebaseFirestore.getInstance().collection("float_plans").add(floatPlan.toMap())
                    }
                }
        } catch (e: Exception) {
            // Handle error
        }
    }
}