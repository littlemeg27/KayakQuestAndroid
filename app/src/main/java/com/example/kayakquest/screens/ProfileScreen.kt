package com.example.kayakquest.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kayakquest.data.KayakerProfile
import com.example.kayakquest.profile.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: ProfileViewModel = viewModel())
{
    val profile by viewModel.profile.collectAsState()

    val scrollState = rememberScrollState()

    // Local state for editable fields
    var name by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var kayakMake by remember { mutableStateOf("") }
    var kayakModel by remember { mutableStateOf("") }
    var kayakLength by remember { mutableStateOf("") }
    var kayakColor by remember { mutableStateOf("") }
    var safetyNotes by remember { mutableStateOf("") }
    var vehicleMake by remember { mutableStateOf("") }
    var vehicleModel by remember { mutableStateOf("") }
    var vehicleColor by remember { mutableStateOf("") }
    var plateNumber by remember { mutableStateOf("") }

    // Sync local state when profile loads/changes
    LaunchedEffect(profile) {
        name = profile?.name ?: ""
        gender = profile?.gender ?: ""
        age = profile?.age?.toString() ?: ""
        address = profile?.address ?: ""
        city = profile?.city ?: ""
        state = profile?.state ?: ""
        email = profile?.email ?: ""
        phone = profile?.phone ?: ""
        kayakMake = profile?.kayakMake ?: ""
        kayakModel = profile?.kayakModel ?: ""
        kayakLength = profile?.kayakLength ?: ""
        kayakColor = profile?.kayakColor ?: ""
        safetyNotes = profile?.safetyEquipmentNotes ?: ""
        vehicleMake = profile?.vehicleMake ?: ""
        vehicleModel = profile?.vehicleModel ?: ""
        vehicleColor = profile?.vehicleColor ?: ""
        plateNumber = profile?.plateNumber ?: ""
    }

    // Your full dropdown options
    val genderOptions = listOf("Male", "Female")
    val stateOptions = listOf(
        "Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware",
        "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky",
        "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota", "Mississippi",
        "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico",
        "New York", "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania",
        "Rhode Island", "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Vermont",
        "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming"
    )
    val kayakOptions = listOf(
        "3 Waters Kayaks", "Abitibi & Co.", "Advanced Elements", "AIRE", "Aqua Marina", "Aquaglide",
        "Aquarius", "Arctic Water Sport", "Ascend", "Atlantis Kayaks", "Bliss-Stick", "Bonafide Kayaks",
        "Boreal Design", "BKC (Brooklyn Kayak Company)", "Current Designs", "Dagger Kayaks", "Delta Kayaks",
        "Eddyline Kayaks", "Epic Kayaks", "Evoke Kayaks", "FeelFree Kayaks", "Field & Stream", "Grabner",
        "Hobie", "Impex", "Jackson Kayak", "Lifetime", "Lightning Kayaks", "Liquid Logic", "Malibu",
        "Native Watercraft", "NC Kayaks", "NuCanoe", "Ocean Kayak", "Old Town", "Oru Kayak", "P&H Sea Kayaks",
        "Pelican", "Perception", "Point 65", "Prijon", "Pyranha", "Riot", "RTM Kayaks", "Seaward", "Sevylor",
        "Stellar", "Sun Dolphin", "Tahe Marine", "Vibe", "Wave Sport", "WaveWalk", "Wilderness Systems"
    )
    val kayakLengths = listOf(
        "5.75 ft", "6 ft", "6.42 ft", "7 ft", "7.17 ft", "8 ft", "8.33 ft", "8.42 ft", "8.75 ft",
        "9 ft", "9.25 ft", "9.42 ft", "9.5 ft", "9.67 ft", "9.75 ft", "9.83 ft", "10.0 ft", "10.33 ft",
        "10.42 ft", "10.5 ft", "10.67 ft", "10.75 ft", "11.0 ft", "11.17 ft", "11.25 ft", "11.33 ft",
        "11.42 ft", "11.67 ft", "11.75 ft", "12.0 ft", "12.08 ft", "12.17 ft", "12.25 ft", "12.33 ft",
        "12.42 ft", "12.5 ft", "12.58 ft", "12.67 ft", "12.75 ft", "12.83 ft", "13.0 ft", "13.08 ft",
        "13.17 ft", "13.25 ft", "13.33 ft", "13.42 ft", "13.5 ft", "13.58 ft", "13.67 ft", "13.75 ft",
        "14 ft", "14.08 ft", "14.25 ft", "14.33 ft", "14.42 ft", "14.58 ft", "15 ft", "15.17 ft",
        "15.58 ft", "15.67 ft", "15.75 ft", "16 ft", "16.08 ft", "16.42 ft", "16.67 ft", "16.92 ft",
        "17 ft", "17.08 ft", "17.25 ft", "17.5 ft", "17.58 ft", "18 ft", "18.25 ft", "19.0 ft"
    )
    val kayakColors = listOf(
        "Aurora Borealis", "Battleship Grey Camo", "Black", "Blue", "British Racing Green", "Cinder",
        "Cream", "Dark Blue", "Desert Sunset", "Dune Camo", "Ember", "Ember Camo", "Forest Green",
        "Gray/Grey", "Green", "Horizon", "Lava", "Light Blue", "Limetreuse", "Lunar Blue", "Melon Yellow",
        "Moss", "Moss Camo", "Multiple/Mixed Colors", "Orange", "Papaya Orange", "Pink", "Purple",
        "Quill", "Red", "Red Hibiscus", "Red/Cedar", "Seagrass Green", "Slate Blue", "Solar Yellow",
        "Spring Green", "Steel", "Steel Camo", "Sunrise Camo", "Tan", "Teal", "White", "Wine Red", "Yellow"
    )
    val vehicleModels = listOf(
        "Acura", "Alfa Romeo", "AM General", "Aston Martin", "Audi", "Bentley", "BMW", "Bugatti", "Buick",
        "Cadillac", "Chevrolet", "Chrysler", "Daewoo", "Dodge", "Eagle", "Ferrari", "FIAT", "Fisker",
        "Ford", "Genesis", "Geo", "GMC", "Honda", "HUMMER", "Hyundai", "INEOS", "INFINITI", "Isuzu",
        "Jaguar", "Jeep", "Karma", "Kia", "Lamborghini", "Land Rover", "Lexus", "Lincoln", "Lotus", "Lucid",
        "Maserati", "Maybach", "Mazda", "McLaren", "Mercedes-Benz", "Mercury", "MINI", "Mitsubishi",
        "Nissan", "Oldsmobile", "Panoz", "Plymouth", "Pontiac", "Polestar", "Porsche", "Ram", "Rivian",
        "Rolls-Royce", "Saab", "Saturn", "Scion", "smart", "Spyker", "Subaru", "Suzuki", "Tesla", "Toyota",
        "VinFast", "Volkswagen", "Volvo"
    )
    val vehicleColors = listOf(
        "Beige", "Black", "Blue", "Bronze", "Brown", "Gold", "Gray", "Green", "Maroon", "Orange", "Pink",
        "Purple", "Red", "Silver", "Teal", "White", "Yellow"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Your KayakQuest Profile", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(24.dp))

        // Personal Information Section
        ProfileSection("Personal Information") {
            EditableField("Name", name) { name = it }
            DropdownField("Gender", gender, genderOptions) { gender = it }
            EditableField("Age", age, KeyboardType.Number) { age = it.filter { c -> c.isDigit() } }
            EditableField("Email", email, KeyboardType.Email) { email = it }
            EditableField("Phone", phone, KeyboardType.Phone) { phone = it }
            EditableField("Address", address) { address = it }
            EditableField("City", city) { city = it }
            DropdownField("State", state, stateOptions) { state = it }
        }

        // Kayak Details Section
        ProfileSection("Kayak Details") {
            DropdownField("Kayak Make", kayakMake, kayakOptions) { kayakMake = it }
            DropdownField("Kayak Length", kayakLength, kayakLengths) { kayakLength = it }
            DropdownField("Kayak Color", kayakColor, kayakColors) { kayakColor = it }
            EditableField("Kayak Model", kayakModel) { kayakModel = it }
            EditableField("Safety Equipment Notes", safetyNotes, multiline = true) { safetyNotes = it }
        }

        // Vehicle Information Section
        ProfileSection("Vehicle Information") {
            DropdownField("Vehicle Make", vehicleMake, vehicleModels) { vehicleMake = it }
            EditableField("Vehicle Model", vehicleModel) { vehicleModel = it }
            DropdownField("Vehicle Color", vehicleColor, vehicleColors) { vehicleColor = it }
            EditableField("Plate Number", plateNumber) { plateNumber = it }
        }

        Spacer(Modifier.height(32.dp))

        // Save Button
        Button(onClick = {
            val updatedProfile = KayakerProfile(
                userId = profile?.userId ?: "",
                name = name,
                gender = gender,
                age = age.toIntOrNull() ?: 0,
                address = address,
                city = city,
                state = state,
                email = email,
                phone = phone,
                kayakMake = kayakMake,
                kayakModel = kayakModel,
                kayakLength = kayakLength,
                kayakColor = kayakColor,
                safetyEquipmentNotes = safetyNotes,
                vehicleMake = vehicleMake,
                vehicleModel = vehicleModel,
                vehicleColor = vehicleColor,
                plateNumber = plateNumber
            )
            viewModel.saveProfile(updatedProfile)
        }) {
            Text("Save Profile")
        }

        Spacer(Modifier.height(64.dp))
    }
}

// Reusable Section Card
@Composable
fun ProfileSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}

// Reusable Editable Field
@Composable
fun EditableField(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    multiline: Boolean = false,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        maxLines = if (multiline) 5 else 1
    )
}

// Reusable Dropdown Field
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            readOnly = true,
            value = value,
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryEditable)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = {
                    onSelect(option)
                    expanded = false
                })
            }
        }
    }
}
