package com.example.resumebuilder.ui.screens.forms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.resumebuilder.ViewModels.ResumeViewModel
import com.example.resumebuilder.model.PersonalInfo
import com.example.resumebuilder.ui.components.FormField
import com.example.resumebuilder.ui.components.PrimaryButton
import com.example.resumebuilder.ui.components.ProgressHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    viewModel: ResumeViewModel = hiltViewModel()
) {
    val resumeData by viewModel.currentResumeData
    val uiState by viewModel.uiState
    val personalInfo = resumeData.personalInfo

    // Local state for form fields
    var fullName by remember { mutableStateOf(personalInfo.fullName) }
    var jobTitle by remember { mutableStateOf(personalInfo.jobTitle) }
    var email by remember { mutableStateOf(personalInfo.email) }
    var phone by remember { mutableStateOf(personalInfo.phone) }
    var linkedIn by remember { mutableStateOf(personalInfo.linkedIn) }
    var github by remember { mutableStateOf(personalInfo.github) }
    var website by remember { mutableStateOf(personalInfo.website) }
    var address by remember { mutableStateOf(personalInfo.address) }
    var city by remember { mutableStateOf(personalInfo.city) }
    var state by remember { mutableStateOf(personalInfo.state) }
    var zipCode by remember { mutableStateOf(personalInfo.zipCode) }
    var country by remember { mutableStateOf(personalInfo.country) }
    var summary by remember { mutableStateOf(personalInfo.summary) }

    // Validation states
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var jobTitleError by remember { mutableStateOf<String?>(null) }

    // Auto-save effect
    LaunchedEffect(
        fullName, jobTitle, email, phone, linkedIn, github, website,
        address, city, state, zipCode, country, summary
    ) {
        if (fullName.isNotBlank() || email.isNotBlank()) {
            val updatedPersonalInfo = PersonalInfo(
                fullName = fullName,
                jobTitle = jobTitle,
                email = email,
                phone = phone,
                linkedIn = linkedIn,
                github = github,
                website = website,
                address = address,
                city = city,
                state = state,
                zipCode = zipCode,
                country = country,
                summary = summary
            )
            viewModel.updatePersonalInfo(updatedPersonalInfo)
        }
    }

    // Validation function
    fun validateForm(): Boolean {
        nameError = if (fullName.isBlank()) "Full name is required" else null
        emailError = when {
            email.isBlank() -> "Email is required"
            !email.contains("@") -> "Please enter a valid email"
            else -> null
        }
        jobTitleError = if (jobTitle.isBlank()) "Job title is required" else null

        return nameError == null && emailError == null && jobTitleError == null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Personal Information") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            ProgressHeader(
                stepLabel = "Step 1 of 8",
                totalSteps = 8,
                currentStepIndex = 0
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Show loading indicator
            if (uiState.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Show error if exists
            uiState.error?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Scrollable form content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Basic Information Section
                Text(
                    text = "Basic Information",
                    style = MaterialTheme.typography.titleMedium
                )

                FormField(
                    label = "Full Name *",
                    value = fullName,
                    onValueChange = {
                        fullName = it
                        nameError = null
                    },
                    placeholder = "John Doe",
                    isError = nameError != null,
                    errorMessage = nameError
                )

                FormField(
                    label = "Professional Title *",
                    value = jobTitle,
                    onValueChange = {
                        jobTitle = it
                        jobTitleError = null
                    },
                    placeholder = "Software Engineer",
                    isError = jobTitleError != null,
                    errorMessage = jobTitleError
                )

                // Contact Information Section
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Contact Information",
                    style = MaterialTheme.typography.titleMedium
                )

                FormField(
                    label = "Email Address *",
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = null
                    },
                    placeholder = "john.doe@example.com",
                    keyboardType = KeyboardType.Email,
                    isError = emailError != null,
                    errorMessage = emailError
                )

                FormField(
                    label = "Phone Number",
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = "+1 (555) 123-4567",
                    keyboardType = KeyboardType.Phone
                )

                // Online Presence Section
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Online Presence",
                    style = MaterialTheme.typography.titleMedium
                )

                FormField(
                    label = "LinkedIn Profile",
                    value = linkedIn,
                    onValueChange = { linkedIn = it },
                    placeholder = "https://linkedin.com/in/johndoe",
                    keyboardType = KeyboardType.Uri
                )

                FormField(
                    label = "GitHub Profile",
                    value = github,
                    onValueChange = { github = it },
                    placeholder = "https://github.com/johndoe",
                    keyboardType = KeyboardType.Uri
                )

                FormField(
                    label = "Personal Website",
                    value = website,
                    onValueChange = { website = it },
                    placeholder = "https://johndoe.com",
                    keyboardType = KeyboardType.Uri
                )

                // Location Section
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Location",
                    style = MaterialTheme.typography.titleMedium
                )

                FormField(
                    label = "Street Address",
                    value = address,
                    onValueChange = { address = it },
                    placeholder = "123 Main Street"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FormField(
                        label = "City",
                        value = city,
                        onValueChange = { city = it },
                        placeholder = "New York",
                        modifier = Modifier.weight(1f)
                    )

                    FormField(
                        label = "State",
                        value = state,
                        onValueChange = { state = it },
                        placeholder = "NY",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FormField(
                        label = "ZIP Code",
                        value = zipCode,
                        onValueChange = { zipCode = it },
                        placeholder = "10001",
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f)
                    )

                    FormField(
                        label = "Country",
                        value = country,
                        onValueChange = { country = it },
                        placeholder = "United States",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Professional Summary Section
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Professional Summary",
                    style = MaterialTheme.typography.titleMedium
                )

                FormField(
                    label = "Summary",
                    value = summary,
                    onValueChange = { summary = it },
                    placeholder = "Brief overview of your professional background, key skills, and career objectives...",
                    singleLine = false,
                    minLines = 4
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Save & Continue button
            PrimaryButton(
                text = "Save & Continue",
                onClick = {
                    if (validateForm()) {
                        onNext()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            )
        }
    }
}