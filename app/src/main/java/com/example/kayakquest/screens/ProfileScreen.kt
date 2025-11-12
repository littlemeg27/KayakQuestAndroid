package com.example.kayakquest.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kayakquest.data.KayakerProfile
import com.example.kayakquest.viewmodels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: ProfileViewModel = viewModel())
{
    val profile by viewModel.profile.observeAsState(null)  // Nullable initial state

    val userId = remember { mutableStateOf(profile?.userId ?: "") }
    val name = remember { mutableStateOf(profile?.name ?: "") }
    val gender = remember { mutableStateOf(profile?.gender ?: "") }
    val age = remember { mutableStateOf(profile?.age?.toString() ?: "") }
    val address = remember { mutableStateOf(profile?.address ?: "") }
    val city = remember { mutableStateOf(profile?.city ?: "") }
    val state = remember { mutableStateOf(profile?.state ?: "") }
    val email = remember { mutableStateOf(profile?.email ?: "") }
    val phone = remember { mutableStateOf(profile?.phone ?: "") }
    val kayakMake = remember { mutableStateOf(profile?.kayakMake ?: "") }
    val kayakModel = remember { mutableStateOf(profile?.kayakModel ?: "") }
    val kayakLength = remember { mutableStateOf(profile?.kayakLength ?: "") }
    val kayakColor = remember { mutableStateOf(profile?.kayakColor ?: "") }
    val safetyEquipmentNotes = remember { mutableStateOf(profile?.safetyEquipmentNotes ?: "") }
    val vehicleMake = remember { mutableStateOf(profile?.vehicleMake ?: "") }
    val vehicleModel = remember { mutableStateOf(profile?.vehicleModel ?: "") }
    val vehicleColor = remember { mutableStateOf(profile?.vehicleColor ?: "") }
    val plateNumber = remember { mutableStateOf(profile?.plateNumber ?: "") }

    LaunchedEffect(profile) {
        userId.value = profile?.userId ?: ""
        name.value = profile?.name ?: ""
        gender.value = profile?.gender ?: ""
        age.value = profile?.age?.toString() ?: ""
        address.value = profile?.address ?: ""
        city.value = profile?.city ?: ""
        state.value = profile?.state ?: ""
        email.value = profile?.email ?: ""
        phone.value = profile?.phone ?: ""
        kayakMake.value = profile?.kayakMake ?: ""
        kayakModel.value = profile?.kayakModel ?: ""
        kayakLength.value = profile?.kayakLength ?: ""
        kayakColor.value = profile?.kayakColor ?: ""
        safetyEquipmentNotes.value = profile?.safetyEquipmentNotes ?: ""
        vehicleMake.value = profile?.vehicleMake ?: ""
        vehicleModel.value = profile?.vehicleModel ?: ""
        vehicleColor.value = profile?.vehicleColor ?: ""
        plateNumber.value = profile?.plateNumber ?: ""
    }

    val genderOptions = listOf("Male", "Female")

    val stateOptions = listOf("Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware",
        "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana",
        "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota", "Mississippi", "Missouri", "Montana",
        "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico", "New York", "North Carolina",
        "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina",
        "South Dakota", "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington",
        "West Virginia", "Wisconsin", "Wyoming")

    val kayakOptions = listOf("3 Waters Kayaks", "Abitibi & Co.", "Advanced Elements", "AIRE", "Aqua Marina", "Aquaglide",
        "Aquarius", "Arctic Water Sport", "Ascend", "Atlantis Kayaks", "Bliss-Stick", "Bonafide Kayaks",
        "Boreal Design", "BKC (Brooklyn Kayak Company)", "Current Designs", "Dagger Kayaks", "Delta Kayaks",
        "Eddyline Kayaks", "Epic Kayaks", "Evoke Kayaks", "FeelFree Kayaks", "Field & Stream", "Grabner",
        "Hobie", "Impex", "Jackson Kayak", "Lifetime", "Lightning Kayaks", "Liquid Logic", "Malibu",
        "Native Watercraft", "NC Kayaks", "NuCanoe", "Ocean Kayak", "Old Town", "Oru Kayak", "P&H Sea Kayaks",
        "Pelican", "Perception", "Point 65", "Prijon", "Pyranha", "Riot", "RTM Kayaks", "Seaward", "Sevylor",
        "Stellar", "Sun Dolphin", "Tahe Marine", "Vibe", "Wave Sport", "WaveWalk", "Wilderness Systems")

    val kayakLengths = listOf("5.75 ft", "6 ft", "6.42 ft", "7 ft", "7.17 ft", "8 ft", "8.33 ft", "8.42 ft", "8.75 ft", "9 ft", "9.25 ft",
        "9.42 ft", "9.5 ft", "9.67 ft", "9.75 ft", "9.83 ft", "10.0 ft", "10.33 ft", "10.42 ft", "10.5 ft",
        "10.67 ft", "10.75 ft", "11.0 ft", "11.17 ft",  "11.25 ft", "11.33 ft", "11.42 ft", "11.67 ft", "11.75 ft",
        "12.0 ft", "12.08 ft", "12.17 ft", "12.25 ft", "12.33 ft", "12.42 ft", "12.5 ft", "12.58 ft", "12.67 ft",
        "12.75 ft", "12.83 ft", "13.0 ft", "13.08 ft", "13.17 ft", "13.25 ft", "13.33 ft", "13.42 ft", "13.5 ft",
        "13.58 ft", "13.67 ft", "13.75 ft", "14 ft", "14.08 ft", "14.25 ft",  "14.33 ft", "14.42 ft", "14.58 ft",
        "15 ft", "15.17 ft", "15.58 ft", "15.67 ft", "15.75 ft", "16 ft", "16.08 ft", "16.42 ft", "16.67 ft",
        "16.92 ft", "17 ft", "17.08 ft", "17.25 ft", "17.5 ft", "17.58 ft", "18 ft", "18.25 ft", "19.0 ft")

    val kayakColors = listOf("Aurora Borealis", "Battleship Grey Camo", "Black", "Blue", "British Racing Green", "Cinder", "Cream",
        "Dark Blue", "Desert Sunset", "Dune Camo", "Ember", "Ember Camo", "Forest Green", "Gray/Grey", "Green",
        "Horizon", "Lava", "Light Blue", "Lava", "Limetreuse", "Lunar Blue", "Lava", "Melon Yellow", "Moss", "Moss Camo",
        "Multiple/Mixed Colors", "Orange", "Papaya Orange", "Pink", "Purple", "Quill", "Red", "Red Hibiscus",
        "Red/Cedar", "Seagrass Green", "Slate Blue", "Solar Yellow", "Spring Green", "Steel", "Steel Camo",
        "Sunrise Camo", "Tan", "Teal", "White", "Wine Red", "Yellow")

    Column(modifier = Modifier.padding(16.dp))
    {
        OutlinedTextField(value = userId.value, onValueChange = { userId.value = it }, label = { Text("userId") })
        OutlinedTextField(value = name.value, onValueChange = { name.value = it }, label = { Text("Name") })

        // Gender Dropdown
        var genderExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = genderExpanded,
            onExpandedChange = { genderExpanded = !genderExpanded }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = gender.value,
                onValueChange = { },
                label = { Text("Gender") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = genderExpanded,
                onDismissRequest = { genderExpanded = false }
            )
            {
                genderOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            gender.value = option
                            genderExpanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = age.value,
            onValueChange = { age.value = it.filter { char -> char.isDigit() } },
            label = { Text("Age") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        OutlinedTextField(value = address.value, onValueChange = { address.value = it }, label = { Text("Address") })
        OutlinedTextField(value = city.value, onValueChange = { city.value = it }, label = { Text("City") })

        // State Dropdown
        var stateExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = stateExpanded,
            onExpandedChange = { stateExpanded = !stateExpanded }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = state.value,
                onValueChange = { },
                label = { Text("State") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stateExpanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = stateExpanded,
                onDismissRequest = { stateExpanded = false }
            )
            {
                stateOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            state.value = option
                            stateExpanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(value = email.value, onValueChange = { email.value = it }, label = { Text("Email") })
        OutlinedTextField(value = phone.value, onValueChange = { phone.value = it }, label = { Text("Phone") })

        // Kayak Make Dropdown
        var kayakMakeExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = kayakMakeExpanded,
            onExpandedChange = { kayakMakeExpanded = !kayakMakeExpanded }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = kayakMake.value,
                onValueChange = { },
                label = { Text("Kayak Make") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = kayakMakeExpanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = kayakMakeExpanded,
                onDismissRequest = { kayakMakeExpanded = false }
            )
            {
                kayakOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            kayakMake.value = option
                            kayakMakeExpanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(value = kayakModel.value, onValueChange = { kayakModel.value = it }, label = { Text("Kayak Model") })

        // Kayak Length Dropdown
        var kayakLengthExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = kayakLengthExpanded,
            onExpandedChange = { kayakLengthExpanded = !kayakLengthExpanded }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = kayakLength.value,
                onValueChange = { },
                label = { Text("Kayak Length") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = kayakLengthExpanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = kayakLengthExpanded,
                onDismissRequest = { kayakLengthExpanded = false }
            )
            {
                kayakLengths.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            kayakLength.value = option
                            kayakLengthExpanded = false
                        }
                    )
                }
            }
        }

        // Kayak Color Dropdown
        var kayakColorExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = kayakColorExpanded,
            onExpandedChange = { kayakColorExpanded = !kayakColorExpanded }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = kayakColor.value,
                onValueChange = { },
                label = { Text("Kayak Color") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = kayakColorExpanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = kayakColorExpanded,
                onDismissRequest = { kayakColorExpanded = false }
            )
            {
                kayakColors.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            kayakColor.value = option
                            kayakColorExpanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(value = safetyEquipmentNotes.value, onValueChange = { safetyEquipmentNotes.value = it }, label = { Text("Safety Equipment Notes") })
        OutlinedTextField(value = vehicleMake.value, onValueChange = { vehicleMake.value = it }, label = { Text("Vehicle Make") })
        OutlinedTextField(value = vehicleModel.value, onValueChange = { vehicleModel.value = it }, label = { Text("Vehicle Model") })
        OutlinedTextField(value = vehicleColor.value, onValueChange = { vehicleColor.value = it }, label = { Text("Vehicle Color") })
        OutlinedTextField(value = plateNumber.value, onValueChange = { plateNumber.value = it }, label = { Text("Plate Number") })

        Button(onClick = {
            val updated = profile.copy(
                userId = userId.value,
                name = name.value,
                gender = gender.value,
                age = age.value.toIntOrNull() ?: 0,
                address = address.value,
                city = city.value,
                state = state.value,
                email = email.value,
                phone = phone.value,
                kayakMake = kayakMake.value,
                kayakModel = kayakModel.value,
                kayakLength = kayakLength.value,
                kayakColor = kayakColor.value,
                safetyEquipmentNotes = safetyEquipmentNotes.value,
                vehicleMake = vehicleMake.value,
                vehicleModel = vehicleModel.value,
                vehicleColor = vehicleColor.value,
                plateNumber = plateNumber.value
            )
            viewModel.saveProfile(updated)
        }) {
            Text("Save Profile")
        }
    }
}