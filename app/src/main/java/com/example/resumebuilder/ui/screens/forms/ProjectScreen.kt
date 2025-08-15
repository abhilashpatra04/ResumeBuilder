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
import com.example.resumebuilder.model.Project
import com.example.resumebuilder.ui.components.*
import com.example.resumebuilder.ui.theme.ResumeText
import com.example.resumebuilder.utils.DateFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    viewModel: ResumeViewModel = hiltViewModel()
) {
    val resumeData by viewModel.currentResumeData
    val uiState by viewModel.uiState
    var showAddDialog by remember { mutableStateOf(false) }
    var currentProject by remember { mutableStateOf<Project?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Projects") },
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
                    currentProject = null
                    showAddDialog = true
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Project")
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
                stepLabel = "Step 3 of 8",
                totalSteps = 8,
                currentStepIndex = 2
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

            if (resumeData.projects.isEmpty()) {
                EmptyProjectsState(
                    onAddClick = {
                        currentProject = null
                        showAddDialog = true
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(
                        resumeData.projects,
                        key = { _, project -> project.id }
                    ) { index, project ->
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically() + fadeIn(),
                            exit = slideOutVertically() + fadeOut()
                        ) {
                            ProjectItem(
                                project = project,
                                onEdit = {
                                    currentProject = project
                                    showAddDialog = true
                                },
                                onDelete = {
                                    viewModel.removeProject(project.id)
                                },
                                onMoveUp = if (index > 0) {
                                    { viewModel.moveProjectUp(project.id) }
                                } else null,
                                onMoveDown = if (index < resumeData.projects.size - 1) {
                                    { viewModel.moveProjectDown(project.id) }
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
            ProjectDialog(
                project = currentProject,
                onDismiss = {
                    showAddDialog = false
                    currentProject = null
                },
                onSave = { project ->
                    if (currentProject != null) {
                        viewModel.updateProject(currentProject!!.id, project)
                    } else {
                        viewModel.addProject(project)
                    }
                    showAddDialog = false
                    currentProject = null
                }
            )
        }
    }
}

@Composable
fun EmptyProjectsState(
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
            imageVector = Icons.Default.Construction,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No projects added yet",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Add your projects to showcase your practical experience and skills",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        PrimaryButton(
            text = "Add Project",
            onClick = onAddClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ProjectItem(
    project: Project,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMoveUp: (() -> Unit)? = null,
    onMoveDown: (() -> Unit)? = null
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    SectionCard(
        title = project.name,
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
        if (project.role.isNotBlank()) {
            Text(
                text = project.role,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Text(
            text = project.getFormattedDuration(),
            style = ResumeText.Muted
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = project.description,
            style = MaterialTheme.typography.bodyMedium
        )

        if (project.technologies.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Technologies: ${project.technologies.joinToString(", ")}",
                style = ResumeText.Muted
            )
        }

        if (project.features.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Key Features:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            project.features.take(2).forEach { feature ->
                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                    Text(
                        text = "• ",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = feature,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (project.features.size > 2) {
                Text(
                    text = "... and ${project.features.size - 2} more features",
                    style = ResumeText.Muted,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }

        // Show links if available
        Row(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (project.githubUrl.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = "GitHub",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "GitHub",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (project.demoUrl.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Launch,
                        contentDescription = "Demo",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Demo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete Project") },
            text = { Text("Are you sure you want to delete this project? This action cannot be undone.") },
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
fun ProjectDialog(
    project: Project?,
    onDismiss: () -> Unit,
    onSave: (Project) -> Unit
) {
    val isEditing = project != null

    // Form state
    var name by remember { mutableStateOf(project?.name ?: "") }
    var description by remember { mutableStateOf(project?.description ?: "") }
    var role by remember { mutableStateOf(project?.role ?: "") }
    var startDate by remember {
        mutableStateOf(
            project?.startDate?.let {
                DateFormatter.formatMonthYear(it)
            } ?: ""
        )
    }
    var endDate by remember {
        mutableStateOf(
            project?.endDate?.let {
                DateFormatter.formatMonthYear(it)
            } ?: ""
        )
    }
    var isOngoing by remember { mutableStateOf(project?.isOngoing ?: false) }
    var technologiesText by remember {
        mutableStateOf(project?.technologies?.joinToString(", ") ?: "")
    }
    var featuresText by remember {
        mutableStateOf(project?.features?.joinToString("\n") ?: "")
    }
    var challenges by remember { mutableStateOf(project?.challenges ?: "") }
    var solutions by remember { mutableStateOf(project?.solutions ?: "") }
    var results by remember { mutableStateOf(project?.results ?: "") }
    var projectUrl by remember { mutableStateOf(project?.projectUrl ?: "") }
    var githubUrl by remember { mutableStateOf(project?.githubUrl ?: "") }
    var demoUrl by remember { mutableStateOf(project?.demoUrl ?: "") }

    // Validation state
    var nameError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }

    fun validateForm(): Boolean {
        nameError = if (name.isBlank()) "Project name is required" else null
        descriptionError = if (description.isBlank()) "Project description is required" else null

        return nameError == null && descriptionError == null
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
                    text = if (isEditing) "Edit Project" else "Add Project",
                    style = MaterialTheme.typography.headlineSmall
                )

                FormField(
                    label = "Project Name *",
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = null
                    },
                    placeholder = "E-commerce Website",
                    isError = nameError != null,
                    errorMessage = nameError,
                    imeAction = ImeAction.Next
                )

                FormField(
                    label = "Your Role",
                    value = role,
                    onValueChange = { role = it },
                    placeholder = "Full Stack Developer",
                    imeAction = ImeAction.Next
                )

                FormField(
                    label = "Project Description *",
                    value = description,
                    onValueChange = {
                        description = it
                        descriptionError = null
                    },
                    placeholder = "A comprehensive e-commerce platform built with modern web technologies...",
                    singleLine = false,
                    minLines = 3,
                    isError = descriptionError != null,
                    errorMessage = descriptionError,
                    imeAction = ImeAction.Next
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DatePickerFormField(
                        label = "Start Date",
                        value = startDate,
                        onValueChange = { startDate = it },
                        placeholder = "MM/YYYY",
                        modifier = Modifier.weight(1f)
                    )

                    DatePickerFormField(
                        label = "End Date",
                        value = endDate,
                        onValueChange = { endDate = it },
                        placeholder = "MM/YYYY",
                        enabled = !isOngoing,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isOngoing,
                        onCheckedChange = {
                            isOngoing = it
                            if (it) endDate = ""
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("This project is ongoing")
                }

                FormField(
                    label = "Technologies Used",
                    value = technologiesText,
                    onValueChange = { technologiesText = it },
                    placeholder = "React, Node.js, MongoDB, Express",
                    supportingText = "Separate technologies with commas",
                    imeAction = ImeAction.Next
                )

                FormField(
                    label = "Key Features",
                    value = featuresText,
                    onValueChange = { featuresText = it },
                    placeholder = "User authentication and authorization\nProduct catalog with search and filtering\nSecure payment processing\nOrder management system",
                    singleLine = false,
                    minLines = 4,
                    supportingText = "Enter each feature on a new line",
                    imeAction = ImeAction.Next
                )

                FormField(
                    label = "Challenges Faced",
                    value = challenges,
                    onValueChange = { challenges = it },
                    placeholder = "Describe the main technical or design challenges you encountered...",
                    singleLine = false,
                    minLines = 2,
                    imeAction = ImeAction.Next
                )

                FormField(
                    label = "Solutions Implemented",
                    value = solutions,
                    onValueChange = { solutions = it },
                    placeholder = "Explain how you solved the challenges...",
                    singleLine = false,
                    minLines = 2,
                    imeAction = ImeAction.Next
                )

                FormField(
                    label = "Results & Impact",
                    value = results,
                    onValueChange = { results = it },
                    placeholder = "Quantify the impact of your project (users, performance improvements, etc.)",
                    singleLine = false,
                    minLines = 2,
                    imeAction = ImeAction.Next
                )

                // Links section
                Text(
                    text = "Project Links",
                    style = MaterialTheme.typography.titleMedium
                )

                FormField(
                    label = "Project URL",
                    value = projectUrl,
                    onValueChange = { projectUrl = it },
                    placeholder = "https://myproject.com",
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Next
                )

                FormField(
                    label = "GitHub Repository",
                    value = githubUrl,
                    onValueChange = { githubUrl = it },
                    placeholder = "https://github.com/username/project",
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Next
                )

                FormField(
                    label = "Live Demo",
                    value = demoUrl,
                    onValueChange = { demoUrl = it },
                    placeholder = "https://demo.myproject.com",
                    keyboardType = KeyboardType.Uri,
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
                                val technologies = technologiesText
                                    .split(",")
                                    .map { it.trim() }
                                    .filter { it.isNotBlank() }

                                val features = featuresText
                                    .split("\n")
                                    .map { it.trim() }
                                    .filter { it.isNotBlank() }

                                val newProject = Project(
                                    id = project?.id ?: 0L,
                                    resumeId = project?.resumeId ?: 0L,
                                    name = name,
                                    description = description,
                                    role = role,
                                    startDate = DateFormatter.parseDateString(startDate),
                                    endDate = if (isOngoing) null else DateFormatter.parseDateString(endDate),
                                    isOngoing = isOngoing,
                                    technologies = technologies,
                                    features = features,
                                    challenges = challenges,
                                    solutions = solutions,
                                    results = results,
                                    projectUrl = projectUrl,
                                    githubUrl = githubUrl,
                                    demoUrl = demoUrl,
                                    images = project?.images ?: emptyList(),
                                    sortOrder = project?.sortOrder ?: 0
                                )

                                onSave(newProject)
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