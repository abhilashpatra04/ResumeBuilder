package com.example.resumebuilder.ui.screens.forms

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.resumebuilder.ViewModels.ResumeViewModel
import com.example.resumebuilder.model.WorkExperience
import com.example.resumebuilder.ui.components.*
import com.example.resumebuilder.ui.theme.ResumeText
import com.example.resumebuilder.utils.DateFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperienceScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    viewModel: ResumeViewModel = hiltViewModel()
) {
    val resumeData by viewModel.currentResumeData
    val uiState by viewModel.uiState
    var showAddDialog by remember { mutableStateOf(false) }
    var currentExperience by remember { mutableStateOf<WorkExperience?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Work Experience") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
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
            ProgressHeader(
                stepLabel = "Step 2 of 8",
                totalSteps = 8,
                currentStepIndex = 1
            )

            // Show loading indicator
            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
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
            }

            if (resumeData.experiences.isEmpty()) {
                EmptyExperienceState(
                    onAddClick = {
                        currentExperience = null
                        showAddDialog = true
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(
                        resumeData.experiences,
                        key = { _, experience -> experience.id }
                    ) { index, experience ->
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically() + fadeIn(),
                            exit = slideOutVertically() + fadeOut()
                        ) {
                            ExperienceItem(
                                experience = experience,
                                onEdit = {
                                    currentExperience = experience
                                    showAddDialog = true
                                },
                                onDelete = {
                                    viewModel.removeWorkExperience(experience.id)
                                },
                                onMoveUp = if (index > 0) {
                                    { viewModel.moveExperienceUp(experience.id) }
                                } else null,
                                onMoveDown = if (index < resumeData.experiences.size - 1) {
                                    { viewModel.moveExperienceDown(experience.id) }
                                } else null
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp)) // FAB space
                    }
                }
            }

            PrimaryButton(
                text = "Save & Continue",
                onClick = onNext,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            )
        }

        if (showAddDialog) {
            ExperienceDialog(
                experience = currentExperience,
                onDismiss = {
                    showAddDialog = false
                    currentExperience = null
                },
                onSave = { experience ->
                    if (currentExperience != null) {
                        viewModel.updateWorkExperience(currentExperience!!.id, experience)
                    } else {
                        viewModel.addWorkExperience(experience)
                    }
                    showAddDialog = false
                    currentExperience = null
                }
            )
        }
    }
}

@Composable
fun EmptyExperienceState(
    onAddClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Work,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No work experience yet",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Add your professional experience to showcase your career journey",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        PrimaryButton(
            text = "Add Work Experience",
            onClick = onAddClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ExperienceItem(
    experience: WorkExperience,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMoveUp: (() -> Unit)? = null,
    onMoveDown: (() -> Unit)? = null
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    SectionCard(
        title = experience.jobTitle,
        trailing = {
            Row {
                // Move up/down buttons for reordering
                onMoveUp?.let {
                    IconButton(onClick = it) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "Move up",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                onMoveDown?.let {
                    IconButton(onClick = it) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Move down",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = { showDeleteConfirmation = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    ) {
        Text(
            text = experience.company,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium
        )

        if (experience.location.isNotBlank()) {
            Text(
                text = experience.location,
                style = ResumeText.Muted
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = experience.getFormattedDuration(),
            style = ResumeText.Muted
        )

        if (experience.description.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = experience.description,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (experience.bulletPoints.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            experience.bulletPoints.take(2).forEach { bullet ->
                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                    Text(
                        text = "• ",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = bullet,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (experience.bulletPoints.size > 2) {
                Text(
                    text = "... and ${experience.bulletPoints.size - 2} more",
                    style = ResumeText.Muted,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }

        if (experience.technologies.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Technologies: ${experience.technologies.joinToString(", ")}",
                style = ResumeText.Muted
            )
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete Experience") },
            text = { Text("Are you sure you want to delete this work experience? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteConfirmation = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperienceDialog(
    experience: WorkExperience?,
    onDismiss: () -> Unit,
    onSave: (WorkExperience) -> Unit
) {
    val isEditing = experience != null

    // Form state
    var jobTitle by remember { mutableStateOf(experience?.jobTitle ?: "") }
    var company by remember { mutableStateOf(experience?.company ?: "") }
    var location by remember { mutableStateOf(experience?.location ?: "") }
    var startDate by remember {
        mutableStateOf(
            experience?.startDate?.let {
                DateFormatter.formatMonthYear(it)
            } ?: ""
        )
    }
    var endDate by remember {
        mutableStateOf(
            experience?.endDate?.let {
                DateFormatter.formatMonthYear(it)
            } ?: ""
        )
    }
    var isCurrentPosition by remember { mutableStateOf(experience?.isCurrentPosition ?: false) }
    var description by remember { mutableStateOf(experience?.description ?: "") }
    var bulletPointsText by remember {
        mutableStateOf(experience?.bulletPoints?.joinToString("\n") ?: "")
    }
    var technologiesText by remember {
        mutableStateOf(experience?.technologies?.joinToString(", ") ?: "")
    }

    // Validation state
    var jobTitleError by remember { mutableStateOf<String?>(null) }
    var companyError by remember { mutableStateOf<String?>(null) }
    var startDateError by remember { mutableStateOf<String?>(null) }
    var endDateError by remember { mutableStateOf<String?>(null) }

    fun validateForm(): Boolean {
        jobTitleError = if (jobTitle.isBlank()) "Job title is required" else null
        companyError = if (company.isBlank()) "Company name is required" else null
        startDateError = if (startDate.isBlank()) "Start date is required" else null
        endDateError = if (!isCurrentPosition && endDate.isBlank()) "End date is required" else null

        return jobTitleError == null && companyError == null &&
                startDateError == null && endDateError == null
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (isEditing) "Edit Experience" else "Add Experience",
                    style = MaterialTheme.typography.headlineSmall
                )

                FormField(
                    label = "Job Title *",
                    value = jobTitle,
                    onValueChange = {
                        jobTitle = it
                        jobTitleError = null
                    },
                    placeholder = "Software Engineer",
                    isError = jobTitleError != null,
                    errorMessage = jobTitleError,
                    imeAction = ImeAction.Next
                )

                FormField(
                    label = "Company *",
                    value = company,
                    onValueChange = {
                        company = it
                        companyError = null
                    },
                    placeholder = "Tech Corp Inc.",
                    isError = companyError != null,
                    errorMessage = companyError,
                    imeAction = ImeAction.Next
                )

                FormField(
                    label = "Location",
                    value = location,
                    onValueChange = { location = it },
                    placeholder = "San Francisco, CA",
                    imeAction = ImeAction.Next
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DatePickerFormField(
                        label = "Start Date *",
                        value = startDate,
                        onValueChange = {
                            startDate = it
                            startDateError = null
                        },
                        placeholder = "MM/YYYY",
                        isError = startDateError != null,
                        errorMessage = startDateError,
                        modifier = Modifier.weight(1f)
                    )

                    DatePickerFormField(
                        label = "End Date",
                        value = endDate,
                        onValueChange = {
                            endDate = it
                            endDateError = null
                        },
                        placeholder = "MM/YYYY",
                        enabled = !isCurrentPosition,
                        isError = endDateError != null,
                        errorMessage = endDateError,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isCurrentPosition,
                        onCheckedChange = {
                            isCurrentPosition = it
                            if (it) {
                                endDate = ""
                                endDateError = null
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("I currently work here")
                }

                FormField(
                    label = "Job Description",
                    value = description,
                    onValueChange = { description = it },
                    placeholder = "Brief description of your role and responsibilities...",
                    singleLine = false,
                    minLines = 3,
                    imeAction = ImeAction.Next
                )

                FormField(
                    label = "Key Achievements & Responsibilities",
                    value = bulletPointsText,
                    onValueChange = { bulletPointsText = it },
                    placeholder = "• Increased team productivity by 30%\n• Led development of key features\n• Mentored junior developers",
                    singleLine = false,
                    minLines = 4,
                    supportingText = "Enter each point on a new line",
                    imeAction = ImeAction.Next
                )

                FormField(
                    label = "Technologies Used",
                    value = technologiesText,
                    onValueChange = { technologiesText = it },
                    placeholder = "React, Node.js, MongoDB, AWS",
                    supportingText = "Separate technologies with commas",
                    imeAction = ImeAction.Done
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (validateForm()) {
                                val bulletPoints = bulletPointsText
                                    .split("\n")
                                    .map { it.trim() }
                                    .filter { it.isNotBlank() }
                                    .map { if (it.startsWith("•")) it.substring(1).trim() else it }

                                val technologies = technologiesText
                                    .split(",")
                                    .map { it.trim() }
                                    .filter { it.isNotBlank() }

                                val newExperience = WorkExperience(
                                    id = experience?.id ?: 0L,
                                    resumeId = experience?.resumeId ?: 0L,
                                    jobTitle = jobTitle,
                                    company = company,
                                    location = location,
                                    startDate = DateFormatter.parseDateString(startDate),
                                    endDate = if (isCurrentPosition) null else DateFormatter.parseDateString(endDate),
                                    isCurrentPosition = isCurrentPosition,
                                    description = description,
                                    bulletPoints = bulletPoints,
                                    achievements = emptyList(),
                                    technologies = technologies,
                                    sortOrder = experience?.sortOrder ?: 0
                                )

                                onSave(newExperience)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}