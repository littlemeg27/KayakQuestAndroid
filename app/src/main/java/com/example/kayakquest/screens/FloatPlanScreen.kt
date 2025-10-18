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
import com.example.kayakquest.Operations.FloatPlan
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
        // Example TextField; add more for all fields
        TextField(
            value = floatPlan.value.kayakerName,
            onValueChange = { newValue -> floatPlan.value = floatPlan.value.copy(kayakerName = newValue) },
            label = { Text("Kayaker Name") }
        )
        // Add other TextFields similarly, e.g.:
        // TextField(value = floatPlan.value.gender, onValueChange = { floatPlan.value = floatPlan.value.copy(gender = it) }, label = { Text("Gender") })
        // For spinners (e.g., gender, state): Use ExposedDropdownMenuBox from material3
        // For dates/times: Use DatePicker and TimePicker from material3 (may require @ExperimentalMaterial3Api)

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

            // Add content as paragraphs
            document.add(Paragraph("Kayaker Name: ${floatPlan.kayakerName}"))
            document.add(Paragraph("Gender: ${floatPlan.gender}"))
            // Add all other fields similarly...

            document.close()

            // Upload to Firebase
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