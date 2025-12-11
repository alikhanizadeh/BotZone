package com.example.botzone.Conferences

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.botzone.Room.Registration.RegistrationEntity
import com.example.botzone.Room.Registration.RegistrationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConferenceRegistrationScreen(
    navController: NavController,
    conferenceId: Int,
    registrationViewModel: RegistrationViewModel
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var company by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }

    // خطاها
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }

    val companies = listOf(
        "Cognito Dynamics", "RoboTech Inc.", "AI Nexus", "NeuralForge", "Quantum Robotics", "Other"
    )
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { TopAppBarSection() },
        bottomBar = {
            ContinueButton(
                onClick = {
                    // اعتبارسنجی
                    nameError = if (fullName.isBlank()) "نام کامل الزامی است" else null
                    emailError = if (!email.endsWith("@gmail.com")) "ایمیل باید @gmail.com داشته باشد" else null
                    phoneError = if (!phone.matches(Regex("^0[0-9]{10}\$"))) "شماره باید 11 رقم و با 0 شروع شود" else null

                    if (nameError == null && emailError == null && phoneError == null) {
                        // ذخیره در دیتابیس
                        scope.launch {val reg = RegistrationEntity(
                            conferenceId = conferenceId,
                            fullName = fullName,
                            email = email,
                            phone = phone,
                            company = company
                        )
                        // فقط یکبار ذخیره کن و id رو بگیر
                        val registrationId: Long = registrationViewModel.saveRegistration(reg)

                        withContext(Dispatchers.Main) {
                            navController.navigate("payment/$conferenceId/$registrationId")
                        }
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            StepIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Your Details", style = MaterialTheme.typography.titleMedium)

            InputField(
                label = "Full Name",
                value = fullName,
                onValueChange = { fullName = it },
                error = nameError,
                keyboardType = KeyboardType.Text
            )
            InputField(
                label = "Email Address",
                value = email,
                onValueChange = { email = it },
                error = emailError,
                keyboardType = KeyboardType.Email
            )
            InputField(
                label = "Phone Number",
                value = phone,
                onValueChange = { if (it.length <= 11) phone = it },
                error = phoneError,
                keyboardType = KeyboardType.Phone
            )

            // Dropdown Company
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                InputField(
                    label = "Company / Organization",
                    value = company ?: "",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    companies.forEach { comp ->
                        DropdownMenuItem(
                            text = { Text(comp) },
                            onClick = {
                                company = comp
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarSection() {
    TopAppBar(
        title = {
            Text(
                text = "Conference Registration",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium
            )
        },
        navigationIcon = {
            IconButton(onClick = { /* Back action */ }) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
            }
        }
    )
}

@Composable
fun StepIndicator() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StepItem(active = true, title = "Personal Info", modifier = Modifier.weight(1f))
            StepItem(active = false, title = "Session Selection", modifier = Modifier.weight(1f))
            StepItem(active = false, title = "Payment", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun StepItem(active: Boolean, title: String, modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(
                    if (active) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f),
                    shape = MaterialTheme.shapes.large
                )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = if (active) MaterialTheme.colorScheme.primary else Color.Gray
        )
    }
}

@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter your $label") },
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            readOnly = readOnly,
            trailingIcon = trailingIcon,
            isError = error != null,
            colors = OutlinedTextFieldDefaults.colors(
                errorBorderColor = Color.Red,
                focusedBorderColor = if (error != null) Color.Red else MaterialTheme.colorScheme.primary
            )
        )
        if (error != null) {
            Text(error, color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun ContinueButton(
    onClick: () -> Unit,
) {
    Surface(
        tonalElevation = 3.dp,
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Button(
                onClick = { onClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    "Continue",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
        }
    }
}

// Preview
@Preview(showBackground = true)
@Composable
fun PreviewConferenceRegistration1() {

}
