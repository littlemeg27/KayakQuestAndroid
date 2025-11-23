package com.example.kayakquest.screens

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kayakquest.data.KayakerProfile
import com.example.kayakquest.operations.FloatPlan
import com.example.kayakquest.viewmodels.ProfileViewModel
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
fun FloatPlanScreen()
{
    val context = LocalContext.current
    val profileViewModel: ProfileViewModel = viewModel()
    val profiles = profileViewModel.profiles.observeAsState(emptyList()).value

    val selectedKayakers = remember { mutableStateListOf<KayakerProfile>() }
    val floatPlan = remember { mutableStateOf(FloatPlan()) }
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ----- Float Plan Fields -----
        item {
            TextField(
                value = floatPlan.value.kayakerName,
                onValueChange = { floatPlan.value = floatPlan.value.copy(kayakerName = it) },
                label = { Text("Kayaker Name") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        // Add more TextFields here the same way...

        // ----- Kayaker Selector -----
        item { Text("Add Kayakers from Profile:", style = MaterialTheme.typography.titleMedium) }
        items(profiles) { profile ->
            Button(
                onClick = { if (!selectedKayakers.contains(profile)) selectedKayakers.add(profile) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add ${profile.name}")
            }
        }

        // ----- Selected Kayakers List -----
        if (selectedKayakers.isNotEmpty()) {
            item {
                Text("Selected Kayakers:", style = MaterialTheme.typography.titleMedium)
            }
            items(selectedKayakers) { kayaker ->
                Text("• ${kayaker.name} (${kayaker.gender}, ${kayaker.age})")
            }
        }

        // ----- Submit Button -----
        item {
            Button(
                onClick = {
                    coroutineScope.launch {
                        createAndUploadPdf(context, floatPlan.value, selectedKayakers)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Float Plan")
            }
        }
    }
}

private suspend fun createAndUploadPdf(context: Context, floatPlan: FloatPlan, selectedKayakers: List<KayakerProfile>)
{
    withContext(Dispatchers.IO)
    {
        try
        {
            val pdfFile = File(context.filesDir, "float_plan.pdf")
            val pdfWriter = PdfWriter(pdfFile.absolutePath)
            val pdfDoc = PdfDocument(pdfWriter)
            val document = Document(pdfDoc)

            document.add(Paragraph("Kayaker Name: ${floatPlan.kayakerName}"))
            document.add(Paragraph("Gender: ${floatPlan.gender}"))
            // Add all other floatPlan fields...

            // Add selected kayakers to PDF
            selectedKayakers.forEachIndexed { index, kayaker ->
                document.add(Paragraph("Kayaker ${index + 1}: ${kayaker.name}"))
                document.add(Paragraph("Gender: ${kayaker.gender}"))
                document.add(Paragraph("Age: ${kayaker.age}"))
                document.add(Paragraph("Address: ${kayaker.address}, ${kayaker.city}, ${kayaker.state}"))
                // Add all other kayaker fields...
            }

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
        }
        catch (e: Exception)
        {
            // Handle error, e.g., log or show toast
            e.printStackTrace()
        }
    }
}