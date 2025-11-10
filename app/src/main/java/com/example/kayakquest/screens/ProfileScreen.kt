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
    val name = remember { mutableStateOf(profile?.name ?: "") }
    val gender = remember { mutableStateOf(profile?.gender ?: "") }
    val age = remember { mutableStateOf(profile?.age.toString()) }
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
        userId.value = profile?.userId ?: ""
        name.value = profile?.name ?: ""
        gender.value = profile?.gender ?: ""
        age.value = profile?.age.toString()
        address.value = profile?.address ?: ""
        city.value = profile?.city ?: ""
        state.value = profile?.state.toString()
        email.value = profile?.email ?: ""
        phone.value = profile?.phone ?: ""
        kayakMake.value = profile?.kayakMake.toString()
        kayakModel.value = profile?.kayakModel ?: ""
        kayakLength.value = profile?.kayakLength ?: ""
        kayakColor.value = profile?.kayakColor.toString()
        safetyEquipmentNotes.value = profile?.safetyEquipmentNotes ?: ""
        vehicleMake.value = profile?.vehicleMake ?: ""
        vehicleModel.value = profile?.vehicleModel.toString()
        vehicleColor.value = profile?.vehicleColor.toString()
        plateNumber.value = profile?.plateNumber.toString()
    }

    Column(modifier = Modifier.padding(16.dp))
    {
        TextField(value = userId.value, onValueChange = { userId.value = it }, label = { Text("userId") })
        TextField(value = gender.value, onValueChange = { gender.value = it }, label = { Text("Gender") })
        TextField(value = age.value, onValueChange = { age.value = it }, label = { Text("Age") })
        TextField(value = address.value, onValueChange = { address.value = it }, label = { Text("Address") })
        TextField(value = city.value, onValueChange = { city.value = it }, label = { Text("City") })
        TextField(value = state.value, onValueChange = { state.value = it }, label = { Text("State") })
        TextField(value = email.value, onValueChange = { email.value = it }, label = { Text("Email") })
        TextField(value = phone.value, onValueChange = { phone.value = it }, label = { Text("phone") })
        TextField(value = kayakMake.value, onValueChange = { kayakMake.value = it }, label = { Text("Kayak Make") })
        TextField(value = kayakLength.value, onValueChange = { kayakLength.value = it }, label = { Text("Kayak Length") })
        TextField(value = kayakColor.value, onValueChange = { kayakColor.value = it }, label = { Text("Kayak Color") })
        TextField(value = safetyEquipmentNotes.value, onValueChange = { safetyEquipmentNotes.value = it }, label = { Text("Safety Equipment Notes") })
        TextField(value = vehicleMake.value, onValueChange = { vehicleMake.value = it }, label = { Text("Vehicle Make") })
        TextField(value = vehicleColor.value, onValueChange = { vehicleColor.value = it }, label = { Text("Vehicle Color") })
        TextField(value = plateNumber.value, onValueChange = { plateNumber.value = it }, label = { Text("plateNumber") })

        Button(onClick =
            {
            val updatedProfile = profile?.copy(
                userId = userId.value,
                name = name.value,
                gender = gender.value,
                age = age.value.toIntOrNull() ?: 0,
                address = address.value,
                city = city.value,
                state = state.value,
                email = email.value,
                phone = phone.value.toIntOrNull() ?: 0,
                kayakMake = kayakMake.value,
                kayakLength = kayakLength.value,
                kayakColor = kayakColor.value,
                safetyEquipmentNotes = safetyEquipmentNotes.value,
                vehicleMake = vehicleMake.value,
                vehicleColor = vehicleColor.value,
                plateNumber = plateNumber.value,

            ) ?: KayakerProfile()
            viewModel.saveProfile(updatedProfile)
        })
        {
            Text("Save Profile")
        }
    }
}