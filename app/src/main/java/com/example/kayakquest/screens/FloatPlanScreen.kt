package com.example.kayakquest.screens

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kayakquest.data.KayakerProfile
import com.example.kayakquest.operations.FloatPlan
import com.example.kayakquest.profile.ProfileViewModel
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
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FloatPlanScreen(viewModel: ProfileViewModel = viewModel()) {
    val context = LocalContext.current
    val profiles by viewModel.profiles.collectAsState()

    val selectedKayakers = remember { mutableStateListOf<KayakerProfile>() }
    val floatPlan = remember { mutableStateOf(FloatPlan()) }
    val coroutineScope = rememberCoroutineScope()

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Pre-fill safety notes from the current user's profile (if available)
    LaunchedEffect(profiles) {
        val currentUserProfile = profiles.find { it.userId == FirebaseAuth.getInstance().currentUser?.uid }
        if (currentUserProfile != null) {
            floatPlan.value = floatPlan.value.copy(
                safetyEquipmentNotes = currentUserProfile.safetyEquipmentNotes
            )
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Create Float Plan", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))
        }

        // Safety Equipment on Board (pre-filled from profile)
        item {
            OutlinedTextField(
                value = floatPlan.value.safetyEquipmentNotes,
                onValueChange = { floatPlan.value = floatPlan.value.copy(safetyEquipmentNotes = it) },
                label = { Text("List of Safety Equipment on Board") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 5
            )
        }

        // Departure Date
        item {
            OutlinedTextField(
                value = floatPlan.value.departureDate,
                onValueChange = { },
                label = { Text("Departure Date") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Departure Location
        item {
            OutlinedTextField(
                value = floatPlan.value.departureLocation,
                onValueChange = { floatPlan.value = floatPlan.value.copy(departureLocation = it) },
                label = { Text("Departure Location") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Destination
        item {
            OutlinedTextField(
                value = floatPlan.value.destination,
                onValueChange = { floatPlan.value = floatPlan.value.copy(destination = it) },
                label = { Text("Destination") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Estimated Time of Return
        item {
            OutlinedTextField(
                value = floatPlan.value.estimatedReturnTime,
                onValueChange = { },
                label = { Text("Estimated Time of Return") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showTimePicker = true }) {
                        Icon(Icons.Default.AccessTime, contentDescription = "Select Time")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Other Trip Details
        item {
            OutlinedTextField(
                value = floatPlan.value.otherDetails,
                onValueChange = { floatPlan.value = floatPlan.value.copy(otherDetails = it) },
                label = { Text("Other Trip Details") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 5
            )
        }

        // Add Kayakers from Profiles
        item {
            Text("Add Kayakers from Profiles", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
        }
        items(profiles) { profile ->
            Button(
                onClick = { if (!selectedKayakers.contains(profile)) selectedKayakers.add(profile) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add ${profile.name ?: "Unnamed"}")
            }
        }

        // Selected Kayakers List
        if (selectedKayakers.isNotEmpty()) {
            item {
                Text("Selected Kayakers:", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
            }
            items(selectedKayakers) { kayaker ->
                Text("• ${kayaker.name ?: "Unnamed"} (${kayaker.gender}, Age ${kayaker.age})")
            }
        }

        // Submit Button
        item {
            Spacer(Modifier.height(24.dp))
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

    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            title = { Text("Select Departure Date") },
            text = { DatePicker(state = datePickerState) },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(millis))
                        floatPlan.value = floatPlan.value.copy(departureDate = date)
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        )
    }

    // Time Picker Dialog
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Select Estimated Return Time") },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(onClick = {
                    val time = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                    floatPlan.value = floatPlan.value.copy(estimatedReturnTime = time)
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text("Cancel") } }
        )
    }
}

private suspend fun createAndUploadPdf(context: Context, floatPlan: FloatPlan, selectedKayakers: List<KayakerProfile>) {
    withContext(Dispatchers.IO) {
        try {
            val pdfFile = File(context.filesDir, "float_plan_${System.currentTimeMillis()}.pdf")
            val pdfWriter = PdfWriter(pdfFile.absolutePath)
            val pdfDoc = PdfDocument(pdfWriter)
            val document = Document(pdfDoc)

            document.add(Paragraph("Float Plan").setBold())
            document.add(Paragraph("Safety Equipment on Board: ${floatPlan.safetyEquipmentNotes}"))
            document.add(Paragraph("Departure Date: ${floatPlan.departureDate}"))
            document.add(Paragraph("Departure Location: ${floatPlan.departureLocation}"))
            document.add(Paragraph("Destination: ${floatPlan.destination}"))
            document.add(Paragraph("Estimated Time of Return: ${floatPlan.estimatedReturnTime}"))
            document.add(Paragraph("Other Trip Details: ${floatPlan.otherDetails}"))

            document.add(Paragraph("\nSelected Kayakers:").setBold())
            selectedKayakers.forEachIndexed { index, kayaker ->
                document.add(Paragraph("Kayaker ${index + 1}: ${kayaker.name ?: "Unnamed"}"))
                document.add(Paragraph("---"))
            }

            document.close()

            val auth = FirebaseAuth.getInstance()
            val userId = auth.currentUser?.uid ?: return@withContext
            val storageRef = FirebaseStorage.getInstance().reference
            val pdfRef = storageRef.child("float_plans/$userId/${pdfFile.name}")
            pdfRef.putFile(Uri.fromFile(pdfFile)).await()

            val uri = pdfRef.downloadUrl.await()
            floatPlan.pdfUrl = uri.toString()

            FirebaseFirestore.getInstance().collection("float_plans").add(floatPlan.toMap()).await()

            Log.d("FloatPlan", "PDF uploaded: $uri")
        } catch (e: Exception) {
            Log.e("FloatPlan", "Error", e)
        }
    }
}