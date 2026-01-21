package com.example.paddlequest.screens

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.paddlequest.data.KayakerProfile
import com.example.paddlequest.operations.FloatPlan
import com.example.paddlequest.profile.ProfileViewModel
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloatPlanScreen(viewModel: ProfileViewModel = viewModel()) {
    val context = LocalContext.current
    val profiles by viewModel.profiles.collectAsState()

    val selectedKayakers = remember { mutableStateListOf<KayakerProfile>() }
    val floatPlan = remember { mutableStateOf(FloatPlan()) }
    val coroutineScope = rememberCoroutineScope()

    var showDepartureDatePicker by remember { mutableStateOf(false) }
    var showReturnTimePicker by remember { mutableStateOf(false) }

    // Pre-fill safety notes from current user profile if available
    LaunchedEffect(profiles) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val userProfile = profiles.find { it.userId == currentUserId }
        userProfile?.let { profile ->
            floatPlan.value = floatPlan.value.copy(
                safetyEquipmentNotes = profile.safetyEquipmentNotes
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
                label = { Text("Safety Equipment on Board") },
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
                    IconButton(onClick = { showDepartureDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = "Select departure date"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Departure / Put-in Location
        item {
            OutlinedTextField(
                value = floatPlan.value.putInLocation,
                onValueChange = { floatPlan.value = floatPlan.value.copy(putInLocation = it) },
                label = { Text("Departure / Put-in Location") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Destination / Take-out Location
        item {
            OutlinedTextField(
                value = floatPlan.value.takeOutLocation,
                onValueChange = { floatPlan.value = floatPlan.value.copy(takeOutLocation = it) },
                label = { Text("Destination / Take-out Location") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Estimated Time of Return
        item {
            OutlinedTextField(
                value = floatPlan.value.returnTime,
                onValueChange = { },
                label = { Text("Estimated Time of Return") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showReturnTimePicker = true }) {
                        Icon(
                            imageVector = Icons.Filled.Schedule,
                            contentDescription = "Select estimated return time"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Other Trip Details / Notes
        item {
            OutlinedTextField(
                value = floatPlan.value.tripNotes,
                onValueChange = { floatPlan.value = floatPlan.value.copy(tripNotes = it) },
                label = { Text("Other Trip Details / Notes") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 5
            )
        }

        // Add Participants (Kayakers) from Profiles
        item {
            Spacer(Modifier.height(16.dp))
            Text("Add Participants from Profiles", style = MaterialTheme.typography.titleMedium)
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

        if (selectedKayakers.isNotEmpty()) {
            item {
                Spacer(Modifier.height(16.dp))
                Text("Selected Participants:", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
            }
            items(selectedKayakers) { kayaker ->
                Text("• ${kayaker.name ?: "Unnamed"} (${kayaker.gender}, Age ${kayaker.age})")
            }
        }

        // Submit / Generate PDF
        item {
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    coroutineScope.launch {
                        createAndUploadPdf(context, floatPlan.value, selectedKayakers)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = floatPlan.value.departureDate.isNotBlank() &&
                        floatPlan.value.putInLocation.isNotBlank() &&
                        floatPlan.value.takeOutLocation.isNotBlank()
            ) {
                Text("Generate & Submit Float Plan")
            }
        }
    }

    // ───────────────────────────────────────────────
    //   Date Picker Dialog
    // ───────────────────────────────────────────────
    if (showDepartureDatePicker) {
        val datePickerState = rememberDatePickerState()
        AlertDialog(
            onDismissRequest = { showDepartureDatePicker = false },
            title = { Text("Departure Date") },
            text = { DatePicker(state = datePickerState) },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(millis))
                        floatPlan.value = floatPlan.value.copy(departureDate = date)
                    }
                    showDepartureDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDepartureDatePicker = false }) { Text("Cancel") }
            }
        )
    }

    // ───────────────────────────────────────────────
    //   Time Picker Dialog (for return time)
    // ───────────────────────────────────────────────
    if (showReturnTimePicker) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { showReturnTimePicker = false },
            title = { Text("Estimated Return Time") },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(onClick = {
                    val time = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                    floatPlan.value = floatPlan.value.copy(returnTime = time)
                    showReturnTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showReturnTimePicker = false }) { Text("Cancel") }
            }
        )
    }
}

private suspend fun createAndUploadPdf(
    context: Context,
    floatPlan: FloatPlan,
    selectedKayakers: List<KayakerProfile>
) {
    withContext(Dispatchers.IO) {
        try {
            val pdfFile = File(context.filesDir, "float_plan_${System.currentTimeMillis()}.pdf")
            val pdfWriter = PdfWriter(pdfFile.absolutePath)
            val pdfDoc = PdfDocument(pdfWriter)
            val document = Document(pdfDoc)

            document.add(Paragraph("Float Plan").setBold().setFontSize(18f))

            document.add(Paragraph("Safety Equipment on Board:\n${floatPlan.safetyEquipmentNotes}"))
            document.add(Paragraph("Departure Date: ${floatPlan.departureDate}"))
            document.add(Paragraph("Departure / Put-in Location: ${floatPlan.putInLocation}"))
            document.add(Paragraph("Destination / Take-out Location: ${floatPlan.takeOutLocation}"))
            document.add(Paragraph("Estimated Time of Return: ${floatPlan.returnTime}"))
            document.add(Paragraph("Other Trip Details / Notes:\n${floatPlan.tripNotes}"))

            if (selectedKayakers.isNotEmpty()) {
                document.add(Paragraph("\nParticipants:").setBold())
                selectedKayakers.forEachIndexed { index, kayaker ->
                    document.add(Paragraph("Participant ${index + 1}: ${kayaker.name ?: "Unnamed"}"))
                    document.add(Paragraph("   • Gender: ${kayaker.gender}, Age: ${kayaker.age}"))
                    document.add(Paragraph("   • Safety Notes: ${kayaker.safetyEquipmentNotes}"))
                    document.add(Paragraph("---"))
                }
            }

            document.close()

            val auth = FirebaseAuth.getInstance()
            val userId = auth.currentUser?.uid ?: return@withContext
            val storageRef = FirebaseStorage.getInstance().reference
            val pdfRef = storageRef.child("float_plans/$userId/${pdfFile.name}")
            pdfRef.putFile(Uri.fromFile(pdfFile)).await()

            val downloadUrl = pdfRef.downloadUrl.await().toString()
            floatPlan.pdfUrl = downloadUrl

            FirebaseFirestore.getInstance()
                .collection("float_plans")
                .add(floatPlan.toMap())
                .await()

            Log.d("FloatPlan", "PDF created & uploaded: $downloadUrl")
        } catch (e: Exception) {
            Log.e("FloatPlan", "PDF creation/upload failed", e)
        }
    }
}