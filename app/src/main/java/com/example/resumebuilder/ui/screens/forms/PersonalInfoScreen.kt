package com.example.resumebuilder.ui.screens.forms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
    val resumeData by viewModel.resumeData.collectAsState()
    val personalInfo = resumeData.personalInfo
    
    var name by remember { mutableStateOf(personalInfo.name) }
    var email by remember { mutableStateOf(personalInfo.email) }
    var phone by remember { mutableStateOf(personalInfo.phone) }
    var address by remember { mutableStateOf(personalInfo.address) }
    var summary by remember { mutableStateOf(personalInfo.summary) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Personal Information") },
                navigationIcon = { 
                    IconButton(onClick = onBack) { 
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null) 
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
            ProgressHeader(stepLabel = "Step 1 of 3", totalSteps = 3, currentStepIndex = 0)
            Spacer(modifier = Modifier.height(16.dp))
            
            // Scrollable content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FormField(
                    label = "Full Name",
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "John Doe"
                )
                
                FormField(
                    label = "Email",
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "john.doe@example.com"
                )
                
                FormField(
                    label = "Phone",
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = "+1 (555) 123-4567"
                )
                
                FormField(
                    label = "Address",
                    value = address,
                    onValueChange = { address = it },
                    placeholder = "123 Main St, City, State, ZIP"
                )
                
                FormField(
                    label = "Professional Summary",
                    value = summary,
                    onValueChange = { summary = it },
                    placeholder = "Brief overview of your professional background and key strengths",
                    singleLine = false
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Save & Continue button
            PrimaryButton(
                text = "Save & Continue",
                onClick = {
                    // Update the ViewModel with the new personal info
                    viewModel.updatePersonalInfo(
                        PersonalInfo(
                            name = name,
                            email = email,
                            phone = phone,
                            address = address,
                            summary = summary
                        )
                    )
                    onNext()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
