package com.example.kayakquest.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kayakquest.data.KayakerProfile
import com.example.kayakquest.viewmodels.ProfileViewModel
import com.google.firebase.firestore.PropertyName

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = viewModel())
{
    val profile by viewModel.profile.observeAsState(KayakerProfile())

    val userId = remember { mutableStateOf(profile?.userId ?: "") }
    val gender = remember { mutableStateOf(profile?.gender ?: "") }
    val age = remember { mutableStateOf(profile?.age.toString()) }
    val name = remember { mutableStateOf(profile?.name ?: "") }
    val address = remember { mutableStateOf(profile?.address ?: "") }
    val city = remember { mutableStateOf(profile?.city.toString()) }
    val state = remember { mutableStateOf(profile?.state ?: "") }
    val email = remember { mutableStateOf(profile?.email ?: "") }
    val phone = remember { mutableStateOf(profile?.phone.toString()) }
    val kayakMake = remember { mutableStateOf(profile?.kayakMake ?: "") }
    val kayakModel = remember { mutableStateOf(profile?.kayakModel ?: "") }
    val kayakLength = remember { mutableStateOf(profile?.kayakLength.toString()) }
    val kayakColor = remember { mutableStateOf(profile?.kayakColor ?: "") }
    val safetyEquipmentNotes = remember { mutableStateOf(profile?.safetyEquipmentNotes ?: "") }
    val vehicleMake = remember { mutableStateOf(profile?.vehicleMake.toString()) }
    val vehicleModel = remember { mutableStateOf(profile?.vehicleModel.toString()) }
    val vehicleColor = remember { mutableStateOf(profile?.vehicleColor.toString()) }
    val plateNumber = remember { mutableStateOf(profile?.plateNumber.toString()) }


    LaunchedEffect(profile)
    {
        name.value = profile?.name ?: ""
        gender.value = profile?.gender ?: ""
        age.value = profile?.age.toString()
        // Update other states...
    }

    Column(modifier = Modifier.padding(16.dp))
    {
        TextField(value = name.value, onValueChange = { name.value = it }, label = { Text("Name") })
        TextField(value = gender.value, onValueChange = { gender.value = it }, label = { Text("Gender") })
        TextField(value = age.value, onValueChange = { age.value = it }, label = { Text("Age") })
        // Add TextFields for address, kayak info, etc.

        Button(onClick = {
            val updatedProfile = profile?.copy(
                name = name.value,
                gender = gender.value,
                age = age.value.toIntOrNull() ?: 0
                // Update other fields...
            ) ?: KayakerProfile()
            viewModel.saveProfile(updatedProfile)
        }) {
            Text("Save Profile")
        }
    }
}