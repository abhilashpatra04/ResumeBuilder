package com.example.resumebuilder.ui.screens.forms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.resumebuilder.ViewModels.ResumeViewModel
import com.example.resumebuilder.model.Experience
import com.example.resumebuilder.ui.components.FormField
import com.example.resumebuilder.ui.components.PrimaryButton
import com.example.resumebuilder.ui.components.ProgressHeader
import com.example.resumebuilder.ui.components.SectionCard
import com.example.resumebuilder.ui.theme.ResumeText
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperienceScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    viewModel: ResumeViewModel = hiltViewModel()
) {
    val resumeData by viewModel.resumeData.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var currentExperience by remember { mutableStateOf<Experience?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Work Experience") },
                navigationIcon = { 
                    IconButton(onClick = onBack) { 
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null) 
                    } 
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    currentExperience = null
                    showAddDialog = true 
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Experience")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProgressHeader(stepLabel = "Step 2 of 3", totalSteps = 3, currentStepIndex = 1)
            
            if (resumeData.experiences.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Tap the + button to add work experience",
                        style = ResumeText.Muted
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(resumeData.experiences) { experience ->
                        ExperienceItem(
                            experience = experience,
                            onEdit = {
                                currentExperience = experience
                                showAddDialog = true
                            },
                            onDelete = { viewModel.removeExperience(experience.id) }
                        )
                    }
                    item { Spacer(Modifier.height(72.dp)) }
                }
            }
            
            PrimaryButton(
                text = "Save & Continue",
                onClick = onNext,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        if (showAddDialog) {
            ExperienceDialog(
                experience = currentExperience,
                onDismiss = { showAddDialog = false },
                onSave = { experience ->
                    if (currentExperience != null) {
                        viewModel.updateExperience(experience)
                    } else {
                        viewModel.addExperience(experience)
                    }
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun ExperienceItem(
    experience: Experience,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    SectionCard(
        title = experience.position,
        trailing = {
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    ) {
        Text(experience.company, style = ResumeText.Subhead)
        Spacer(Modifier.height(4.dp))
        
        val dateText = if (experience.isCurrentPosition) {
            "${experience.startDate} - Present"
        } else {
            "${experience.startDate} - ${experience.endDate}"
        }
        
        Text(dateText, style = ResumeText.Muted)
        Spacer(Modifier.height(8.dp))
        Text(experience.description)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperienceDialog(
    experience: Experience?,
    onDismiss: () -> Unit,
    onSave: (Experience) -> Unit
) {
    val isEditing = experience != null
    var company by remember { mutableStateOf(experience?.company ?: "") }
    var position by remember { mutableStateOf(experience?.position ?: "") }
    var startDate by remember { mutableStateOf(experience?.startDate ?: "") }
    var endDate by remember { mutableStateOf(experience?.endDate ?: "") }
    var isCurrentPosition by remember { mutableStateOf(experience?.isCurrentPosition ?: false) }
    var description by remember { mutableStateOf(experience?.description ?: "") }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isEditing) "Edit Experience" else "Add Experience",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(16.dp))
                
                FormField(
                    label = "Company",
                    value = company,
                    onValueChange = { company = it },
                    placeholder = "Company name"
                )
                Spacer(Modifier.height(8.dp))
                
                FormField(
                    label = "Position",
                    value = position,
                    onValueChange = { position = it },
                    placeholder = "Your job title"
                )
                Spacer(Modifier.height(8.dp))
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    FormField(
                        label = "Start Date",
                        value = startDate,
                        onValueChange = { startDate = it },
                        placeholder = "MM/YYYY",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    FormField(
                        label = "End Date",
                        value = endDate,
                        onValueChange = { endDate = it },
                        placeholder = "MM/YYYY",
                        modifier = Modifier.weight(1f),
                        enabled = !isCurrentPosition
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isCurrentPosition,
                        onCheckedChange = { isCurrentPosition = it }
                    )
                    Text("Current Position")
                }
                Spacer(Modifier.height(8.dp))
                
                FormField(
                    label = "Description",
                    value = description,
                    onValueChange = { description = it },
                    placeholder = "Describe your responsibilities and achievements",
                    singleLine = false
                )
                Spacer(Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val newExperience = Experience(
                                id = experience?.id ?: UUID.randomUUID().toString(),
                                company = company,
                                position = position,
                                startDate = startDate,
                                endDate = endDate,
                                isCurrentPosition = isCurrentPosition,
                                description = description
                            )
                            onSave(newExperience)
                        },
                        enabled = company.isNotBlank() && position.isNotBlank() && startDate.isNotBlank() 
                            && (isCurrentPosition || endDate.isNotBlank()),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
