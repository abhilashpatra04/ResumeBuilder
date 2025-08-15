package com.example.resumebuilder.ui.screens.forms

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.resumebuilder.model.Education
import com.example.resumebuilder.ui.components.*
import com.example.resumebuilder.ui.theme.ResumeText
import com.example.resumebuilder.utils.DateFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    viewModel: ResumeViewModel = hiltViewModel()
) {
    val resumeData by viewModel.currentResumeData
    val uiState by viewModel.uiState
    var showAddDialog by remember { mutableStateOf(false) }
    var currentEducation by remember { mutableStateOf<Education?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Education") },
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
                    currentEducation = null
                    showAddDialog = true
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Education")
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
                stepLabel = "Step 5 of 8",
                totalSteps = 8,
                currentStepIndex = 4
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

            if (resumeData.education.isEmpty()) {
                EmptyEducationState(
                    onAddClick = {
                        currentEducation = null
                        showAddDialog = true
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(
                        resumeData.education,
                        key = { _, education -> education.id }
                    ) { index, education ->
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically() + fadeIn(),
                            exit = slideOutVertically() + fadeOut()
                        ) {
                            EducationItem(
                                education = education,
                                onEdit = {
                                    currentEducation = education
                                    showAddDialog = true
                                },
                                onDelete = {
                                    viewModel.removeEducation(education.id)
                                },
                                onMoveUp = if (index > 0) {
                                    { viewModel.moveEducationUp(education.id) }
                                } else null,
                                onMoveDown = if (index < resumeData.education.size - 1) {
                                    { viewModel.moveEducationDown(education.id) }
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
            EducationDialog(
                education = currentEducation,
                onDismiss = {
                    showAddDialog = false
                    currentEducation = null
                },
                onSave = { education ->
                    if (currentEducation != null) {
                        viewModel.updateEducation(currentEducation!!.id, education)
                    } else {
                        viewModel.addEducation(education)
                    }
                    showAddDialog = false
                    currentEducation = null
                }
            )
        }
    }
}

@Composable
fun EmptyEducationState(
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
            imageVector = Icons.Default.School,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No education added yet",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Add your educational background to showcase your qualifications",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        PrimaryButton(
            text = "Add Education",
            onClick = onAddClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun EducationItem(
    education: Education,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMoveUp: (() -> Unit)? = null,
    onMoveDown: (() -> Unit)? = null
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    SectionCard(
        title = education.degree + if (education.fieldOfStudy.isNotBlank()) " in ${education.fieldOfStudy}" else "",
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
            text = education.institution,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium
        )

        if (education.location.isNotBlank()) {
            Text(
                text = education.location,
                style = ResumeText.Muted
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = education.getFormattedGraduation(),
            style = ResumeText.Muted
        )

        if (education.gpa.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = education.getFormattedGpa(),
                style = ResumeText.Muted
            )
        }

        if (education.relevantCourses.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Relevant Courses: ${education.relevantCourses.take(3).joinToString(", ")}${if (education.relevantCourses.size > 3) "..." else ""}",
                style = ResumeText.Muted
            )
        }

        if (education.honors.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Honors: ${education.honors.joinToString(", ")}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (education.thesis.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Thesis: ${education.thesis}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete Education") },
            text = { Text("Are you sure you want to delete this education entry? This action cannot be undone.") },
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
fun EducationDialog(
    education: Education?,
    onDismiss: () -> Unit,
    onSave: (Education) -> Unit
) {
    val isEditing = education != null

    // Form state
    var degree by remember { mutableStateOf(education?.degree ?: "") }
    var fieldOfStudy by remember { mutableStateOf(education?.fieldOfStudy ?: "") }
    var institution by remember { mutableStateOf(education?.institution ?: "") }
    var location by remember { mutableStateOf(education?.location ?: "") }
    var graduationDate by remember {
        mutableStateOf(
            education?.graduationDate?.let {
                DateFormatter.formatMonthYear(it)
            } ?: ""
        )
    }
    var gpa by remember { mutableStateOf(education?.gpa ?: "") }
    var maxGpa by remember { mutableStateOf(education?.maxGpa ?: "4.0") }
    var relevantCoursesText by remember {
        mutableStateOf(education?.relevantCourses?.joinToString(", ") ?: "")
    }
    var honorsText by remember {
        mutableStateOf(education?.honors?.joinToString(", ") ?: "")
    }
    var activitiesText by remember {
        mutableStateOf(education?.activities?.joinToString(", ") ?: "")
    }
    var thesis by remember { mutableStateOf(education?.thesis ?: "") }

    // Validation state
    var degreeError by remember { mutableStateOf<String?>(null) }
    var institutionError by remember { mutableStateOf<String?>(null) }
    var gpaError by remember { mutableStateOf<String?>(null) }

    fun validateForm(): Boolean {
        degreeError = if (degree.isBlank()) "Degree is required" else null
        institutionError = if (institution.isBlank()) "Institution name is required" else null
        gpaError = if (gpa.isNotBlank()) {
            try {
                val gpaValue = gpa.toDouble()
                val maxGpaValue = maxGpa.toDoubleOrNull() ?: 4.0
                if (gpaValue > maxGpaValue) "GPA cannot exceed maximum GPA" else null
            } catch (e: NumberFormatException) {
                "Please enter a valid GPA"
            }
        } else null

        return degreeError == null && institutionError == null && gpaError == null
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
                    text = if (isEditing) "Edit Education" else "Add Education",
                    style = MaterialTheme.typography.headlineSmall
                )

                DropdownFormField(
                    label = "Degree *",
                    value = degree,
                    onValueChange = {
                        degree = it
                        degreeError = null
                    },
                    options = listOf(
                        "High School Diploma",
                        "Associate's Degree",
                        "Bachelor's Degree",
                        "Master's Degree",
                        "Doctoral Degree (PhD)",
                        "Professional Degree",
                        "Certificate",
                        "Other"
                    ),
                    isError = degreeError != null,
                    errorMessage = degreeError
                )

                FormField(
                    label = "Field of Study",
                    value = fieldOfStudy,
                    onValueChange = { fieldOfStudy = it },
                    placeholder = "Computer Science",
                    imeAction = ImeAction.Next
                )

                FormField(
                    label = "Institution *",
                    value = institution,
                    onValueChange = {
                        institution = it
                        institutionError = null
                    },
                    placeholder = "University of Technology",
                    isError = institutionError != null,
                    errorMessage = institutionError,
                    imeAction = ImeAction.Next
                )

                FormField(
                    label = "Location",
                    value = location,
                    onValueChange = { location = it },
                    placeholder = "Boston, MA",
                    imeAction = ImeAction.Next
                )

                DatePickerFormField(
                    label = "Graduation Date",
                    value = graduationDate,
                    onValueChange = { graduationDate = it },
                    placeholder = "MM/YYYY",
                    dateFormat = "MM/yyyy"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FormField(
                        label = "GPA",
                        value = gpa,
                        onValueChange = {
                            gpa = it
                            gpaError = null
                        },
                        placeholder = "3.8",
                        keyboardType = KeyboardType.Decimal,
                        isError = gpaError != null,
                        errorMessage = gpaError,
                        modifier = Modifier.weight(1f),
                        imeAction = ImeAction.Next
                    )

                    FormField(
                        label = "Max GPA",
                        value = maxGpa,
                        onValueChange = { maxGpa = it },
                        placeholder = "4.0",
                        keyboardType = KeyboardType.Decimal,
                        modifier = Modifier.weight(1f),
                        imeAction = ImeAction.Next
                    )
                }

                FormField(
                    label = "Relevant Courses",
                    value = relevantCoursesText,
                    onValueChange = { relevantCoursesText = it },
                    placeholder = "Data Structures, Algorithms, Database Systems",
                    supportingText = "Separate courses with commas",
                    imeAction = ImeAction.Next
                )

                FormField(
                    label = "Honors & Awards",
                    value = honorsText,
                    onValueChange = { honorsText = it },
                    placeholder = "Dean's List, Magna Cum Laude, Outstanding Student Award",
                    supportingText = "Separate honors with commas",
                    imeAction = ImeAction.Next
                )

                FormField(
                    label = "Activities & Organizations",
                    value = activitiesText,
                    onValueChange = { activitiesText = it },
                    placeholder = "Student Government, Computer Science Club, Debate Team",
                    supportingText = "Separate activities with commas",
                    imeAction = ImeAction.Next
                )

                FormField(
                    label = "Thesis/Capstone Project",
                    value = thesis,
                    onValueChange = { thesis = it },
                    placeholder = "Title of your thesis or major project",
                    singleLine = false,
                    minLines = 2,
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
                                val relevantCourses = relevantCoursesText
                                    .split(",")
                                    .map { it.trim() }
                                    .filter { it.isNotBlank() }

                                val honors = honorsText
                                    .split(",")
                                    .map { it.trim() }
                                    .filter { it.isNotBlank() }

                                val activities = activitiesText
                                    .split(",")
                                    .map { it.trim() }
                                    .filter { it.isNotBlank() }

                                val newEducation = Education(
                                    id = education?.id ?: 0L,
                                    resumeId = education?.resumeId ?: 0L,
                                    degree = degree,
                                    fieldOfStudy = fieldOfStudy,
                                    institution = institution,
                                    location = location,
                                    graduationDate = DateFormatter.parseDateString(graduationDate),
                                    gpa = gpa,
                                    maxGpa = maxGpa,
                                    relevantCourses = relevantCourses,
                                    honors = honors,
                                    activities = activities,
                                    thesis = thesis,
                                    sortOrder = education?.sortOrder ?: 0
                                )

                                onSave(newEducation)
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